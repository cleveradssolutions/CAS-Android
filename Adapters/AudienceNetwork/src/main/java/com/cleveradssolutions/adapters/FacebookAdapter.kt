package com.cleveradssolutions.adapters

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.cleveradssolutions.adapters.facebook.BuildConfig
import com.cleveradssolutions.adapters.facebook.FacebookBannerAd
import com.cleveradssolutions.adapters.facebook.FacebookNativeAd
import com.cleveradssolutions.adapters.facebook.FacebookScreenAd
import com.cleveradssolutions.adapters.facebook.setWatermark
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork
import com.facebook.ads.AdSettings
import com.facebook.ads.AdSize
import com.facebook.ads.AudienceNetworkActivity
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.BidderTokenProvider
import com.facebook.ads.InterstitialAd
import com.facebook.ads.RewardedVideoAd
import com.facebook.ads.internal.settings.AdInternalSettings


private const val pleaseReadDocsPage =
    "\nPlease read the Additional Meta AudienceNetwork steps page in docs."

class FacebookAdapter : MediationAdapterBase(), AudienceNetworkAds.InitListener {
    override fun getSDKVersion(): String =
        getConstValue(com.facebook.ads.BuildConfig::class.java, "VERSION_NAME")

    override fun getMinSDKVersion(): String? = com.facebook.ads.BuildConfig.VERSION_NAME
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = AudienceNetworkActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.allFormats or
            AdapterSupport.iron or
            AdapterSupport.admob or
            AdapterSupport.max

    override fun getIntegrationError(context: Context): String? = getIntegrationError()

    private fun getIntegrationError(): String? {
        synchronized(AdInternalSettings.sSettingsBundle) {
            if (!AdInternalSettings.sDataProcessingOptionsUpdate.get()) {
                if (getMetaData(AdNetwork.FACEBOOK_DATA_PROCESSING) == null) {
                    return "Audience Network Data Processing Options for US Users is not implemented." + pleaseReadDocsPage
                }
            }
        }
        return null
    }

    override fun initAds(request: MediationInitAdRequest) {
        onUserPrivacyChanged(request.privacy)

        getIntegrationError()?.let {
            debug(it, Log.WARN)
        }

        val builder = AudienceNetworkAds.buildInitSettings(request.context)
        if (request.isTestRequest) {
            // placementIds should be not set for test request
            AdSettings.setDebugBuild(request.isDebugLogsEnabled)
            AdSettings.setTestMode(true)
        } else {
            val mediation = request.getStrParameter(Constants.KEY_MEDIATION_NAME)
            if (mediation == null) {
                request.onFailure(
                    AdError(
                        AdErrorCode.INTERNAL_ERROR, "Required define the mediation service name"
                    )
                )
                return
            }

            val placementIds = ArrayList<String>()
            for (config in request.configurations) {
                for (key in config.parameterKeys) {
                    if (key.endsWith("id") || key.contains("rtb")) {
                        config.getStrParameter(key)?.let {
                            placementIds.add(it)
                        }
                    }
                }
            }

            builder.withMediationService(mediation)
            builder.withPlacementIds(placementIds)
        }

        builder.withInitListener(this).initialize()
    }

    override fun onInitialized(result: AudienceNetworkAds.InitResult) {
        if (result.isSuccess) {
            initRequest.onSuccess()
        } else {
            initRequest.onFailure(AdError(AdErrorCode.NOT_INITIALIZED, result.message))
        }
    }

    @WorkerThread
    override fun collectSignals(request: MediationAdSignalRequest) {
        request.onSuccess(
            BidderTokenProvider.getBidderToken(request.context).orEmpty()
        )
    }

    @MainThread
    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        // Cause java.lang.IllegalArgumentException: Can't create AdSize using this width = 411 && height = 64
        // AdSize.fromWidthAndHeight(adSize.width, adSize.height)
        // Also FB AdSize getHeight() return -1

        val adSize = when (request.adSizeId) {
            AdSizeId.DEFAULT -> AdSize.BANNER_HEIGHT_50
            AdSizeId.LARGE -> AdSize.BANNER_HEIGHT_90
            AdSizeId.MREC -> AdSize.RECTANGLE_HEIGHT_250
            else -> return super.loadAd(request)
        }
        return FacebookBannerAd(request, adSize)
    }

    @MainThread
    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader {
        val view = InterstitialAd(request.context, request.unitId)
        val loader = FacebookScreenAd(request, view)
        view.setWatermark(request)
        view.loadAd(
            view.buildLoadAdConfig()
                .withAdListener(loader)
                .withBid(request.bidResponse)
                .build()
        )
        return loader
    }

    @MainThread
    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader{
        val view = RewardedVideoAd(request.context, request.unitId)
        val loader = FacebookScreenAd(request, view)
        view.setWatermark(request)
        view.loadAd(
            view.buildLoadAdConfig()
                .withAdListener(loader)
                .withBid(request.bidResponse)
                .build()
        )
        return loader
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader =
        FacebookNativeAd(request)

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.isAppliesCOPPA()?.let {
            AdSettings.setMixedAudience(it)
        }

        getMetaData(AdNetwork.FACEBOOK_DATA_PROCESSING)?.let {
            if (it.isEmpty()) {
                AdSettings.setDataProcessingOptions(arrayOf())
            } else if (it == "LDU") {
                AdSettings.setDataProcessingOptions(arrayOf("LDU"), 0, 0)
            } else {
                val param = it.split('_')
                if (param.size == 3) {
                    AdSettings.setDataProcessingOptions(
                        arrayOf(param[0]), param[1].toIntOrNull() ?: 0, param[2].toIntOrNull() ?: 0
                    )
                } else {
                    AdSettings.setDataProcessingOptions(arrayOf(it), 0, 0)
                }
            }
        }
    }
}
