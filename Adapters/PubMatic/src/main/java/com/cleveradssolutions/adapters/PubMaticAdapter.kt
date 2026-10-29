package com.cleveradssolutions.adapters

import androidx.annotation.MainThread
import com.cleveradssolutions.adapters.pubmatic.BannerListener
import com.cleveradssolutions.adapters.pubmatic.BuildConfig
import com.cleveradssolutions.adapters.pubmatic.InterstitialListener
import com.cleveradssolutions.adapters.pubmatic.PubMaticBidLoader
import com.cleveradssolutions.adapters.pubmatic.RewardedListener
import com.cleveradssolutions.adapters.pubmatic.toCASError
import com.cleveradssolutions.internal.AdFormatId
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.pubmatic.sdk.common.OpenWrapSDK
import com.pubmatic.sdk.common.OpenWrapSDKConfig
import com.pubmatic.sdk.common.OpenWrapSDKInitializer
import com.pubmatic.sdk.common.POBAdFormat
import com.pubmatic.sdk.common.POBAdSize
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.common.models.POBApplicationInfo
import com.pubmatic.sdk.nativead.POBNativeAdLoader
import com.pubmatic.sdk.nativead.datatype.POBNativeTemplateType
import com.pubmatic.sdk.nativead.request.POBNativeRequestDataAsset
import com.pubmatic.sdk.nativead.request.POBNativeRequestImageAsset
import com.pubmatic.sdk.nativead.request.POBNativeRequestTitleAsset
import com.pubmatic.sdk.openwrap.banner.POBBannerView
import com.pubmatic.sdk.openwrap.core.nativead.POBNativeDataAssetType
import com.pubmatic.sdk.openwrap.core.nativead.POBNativeImageAssetType
import com.pubmatic.sdk.openwrap.core.signal.POBBiddingHost
import com.pubmatic.sdk.openwrap.core.signal.POBSignalConfig
import com.pubmatic.sdk.openwrap.core.signal.POBSignalGenerator
import com.pubmatic.sdk.openwrap.interstitial.POBInterstitial
import com.pubmatic.sdk.rewardedad.POBRewardedAd
import com.pubmatic.sdk.webrendering.ui.POBFullScreenActivity
import java.net.URL

private const val KEY_PROFILE = "profileid"

class PubMaticAdapter : MediationRtbAdapter(), OpenWrapSDKInitializer.Listener {
    private var publisherId: String = ""
    private var profileId: Int = 0
    private var biddingHost = POBBiddingHost.UNKNOWN

    override fun getSDKVersion(): String = OpenWrapSDK.getVersion()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = POBFullScreenActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.nativ or AdapterSupport.max

    override fun initAds(request: MediationInitAdRequest) {
        // also supported "publisherId" via Core logic
        publisherId = request.requireAppID() ?: return

        val profileIds = ArrayList<Int>()
        for (config in request.configurations) {
            for (key in config.parameterKeys) {
                if (key.contains(KEY_PROFILE)) {
                    val id = config.getIntParameter(key, 0)
                    if (id != 0)
                        profileIds.add(id)
                }
            }
        }
        if (profileIds.isEmpty()) {
            request.onFailure(
                AdError(AdErrorCode.CONFIGURATION_ERROR, "Profile Id not configured")
            )
            return
        }
        profileId = profileIds.first()

        if (request.isDebugLogsEnabled) {
            OpenWrapSDK.setLogLevel(OpenWrapSDK.LogLevel.All)
        }

        onUserPrivacyChanged(request.privacy)

        // Required: a valid Play Store Url for your Android app.
        val appInfo = POBApplicationInfo()
        appInfo.storeURL = try {
            URL(request.targeting.appStoreUrl)
        } catch (_: Throwable) {
            null
        }
        appInfo.keywords = CAS.targetingOptions.keywords?.joinToString()
        OpenWrapSDK.setApplicationInfo(appInfo)

        val config = OpenWrapSDKConfig.Builder(publisherId, profileIds).build()
        OpenWrapSDK.initialize(request.context, config, this)
    }

    override fun onSuccess() {
        initRequest.onSuccess()
    }

    override fun onFailure(error: POBError) {
        initRequest.onFailure(error.toCASError())
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        OpenWrapSDK.allowLocationAccess(CAS.targetingOptions.locationCollectionEnabled)
        privacy.isAppliesCOPPA()?.let {
            OpenWrapSDK.setCoppa(it)
            val isNotCoppa = !it
            OpenWrapSDK.allowAdvertisingId(isNotCoppa)
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val adFormat = when (request.format.value) {
            AdFormatId.BANNER, AdFormatId.INLINE_BANNER -> POBAdFormat.BANNER
            AdFormatId.MREC -> POBAdFormat.MREC
            AdFormatId.INTERSTITIAL -> POBAdFormat.INTERSTITIAL
            AdFormatId.REWARDED -> POBAdFormat.REWARDEDAD
            AdFormatId.NATIVE -> POBAdFormat.NATIVE
            else -> {
                request.onFailure(AdError("Unsupported format"))
                return
            }
        }

        val config = POBSignalConfig.Builder(adFormat)
            .setGpid(request.unitId)
            .build()
        val token = POBSignalGenerator.generateSignal(
            request.context, biddingHost, config
        )
        request.onSuccess(token)
    }

    @MainThread
    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        var profileId = request.getIntParameter(
            request.format.field + "_" + KEY_PROFILE,
            0
        )
        if (profileId == 0) {
            profileId = request.getIntParameter(KEY_PROFILE, this.profileId)
        }
        if (request.format.isAdView) {
            val bannerRequest = request.forBannerAd()
            val size = when (bannerRequest.adSizeId) {
                AdSizeId.DEFAULT -> POBAdSize.BANNER_SIZE_320x50
                AdSizeId.LARGE -> POBAdSize.BANNER_SIZE_728x90
                AdSizeId.MREC -> POBAdSize.BANNER_SIZE_300x250
                else -> return super.loadAd(bannerRequest)
            }

            val view = POBBannerView(request.context, publisherId, profileId, request.unitId, size)
            val bidLoader = PubMaticBidLoader(request, view)
            view.layoutParams = bannerRequest.createAdLayout()
            view.pauseAutoRefresh()
            view.setBidEventListener(bidLoader)
            view.setListener(BannerListener(bidLoader))
            view.loadAd()
            return bidLoader
        }
        when (request.format.value) {
            AdFormatId.INTERSTITIAL -> {
                val view = POBInterstitial(
                    request.context,
                    publisherId,
                    profileId,
                    request.unitId
                )
                val bidLoader = PubMaticBidLoader(request, view)
                view.setBidEventListener(bidLoader)
                view.setListener(InterstitialListener(bidLoader))
                view.loadAd()
                return bidLoader
            }

            AdFormatId.REWARDED -> {
                val view =
                    POBRewardedAd.getRewardedAd(
                        request.context,
                        publisherId,
                        profileId,
                        request.unitId
                    )
                if (view == null) {
                    request.onFailure(
                        AdError(
                            AdErrorCode.CONFIGURATION_ERROR,
                            "Invalid load parameters"
                        )
                    )
                    return null
                } else {
                    val bidLoader = PubMaticBidLoader(request, view)
                    view.setBidEventListener(bidLoader)
                    view.setListener(RewardedListener(bidLoader))
                    view.loadAd()
                    return bidLoader
                }
            }

            AdFormatId.NATIVE -> {
                val loader = POBNativeAdLoader(
                    request.context,
                    publisherId,
                    profileId,
                    request.unitId,
                    POBNativeTemplateType.CUSTOM
                )
                loader.setNativeCustomAssets(
                    listOf(
                        POBNativeRequestDataAsset(POBNativeDataAssetType.CTA_TEXT, false),
                        POBNativeRequestDataAsset(POBNativeDataAssetType.SPONSORED, false),
                        POBNativeRequestDataAsset(POBNativeDataAssetType.DESCRIPTION, false),
                        POBNativeRequestDataAsset(POBNativeDataAssetType.RATING, false),
                        POBNativeRequestDataAsset(POBNativeDataAssetType.PRICE, false),
                        POBNativeRequestTitleAsset(25, false),
                        POBNativeRequestImageAsset(POBNativeImageAssetType.ICON, false, 50, 50),
                        POBNativeRequestImageAsset(
                            POBNativeImageAssetType.MAIN, false, 284, 144
                        ),
                    )
                )
                val bidLoader = PubMaticBidLoader(request, null)
                loader.setBidEventListener(bidLoader)
                loader.setAdLoaderListener(bidLoader)
                loader.loadAd()
                return bidLoader
            }

            else -> return super.fetchAdBid(request)
        }
    }

    @MainThread
    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? =
        if (request.adSizeId == AdSizeId.THIN) {
            super.loadAd(request)
        } else requireBidResponse(request)?.let {
            val view = POBBannerView(request.context)
            val bidLoader = PubMaticBidLoader(request, view)
            view.layoutParams = request.createAdLayout()
            view.pauseAutoRefresh()
            view.setListener(BannerListener(bidLoader))
            view.loadAd(it, biddingHost)
            bidLoader
        }

    @MainThread
    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? =
        requireBidResponse(request)?.let {
            val view = POBInterstitial(request.context)
            val bidLoader = PubMaticBidLoader(request, view)
            view.setListener(InterstitialListener(bidLoader))
            view.loadAd(it, biddingHost)
            bidLoader
        }

    @MainThread
    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? =
        requireBidResponse(request)?.let {
            val view = POBRewardedAd.getRewardedAd(request.context)
            val bidLoader = PubMaticBidLoader(request, view)
            view.setListener(RewardedListener(bidLoader))
            view.loadAd(it, biddingHost)
            bidLoader
        }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? =
        requireBidResponse(request)?.let {
            val loader = POBNativeAdLoader(request.context)
            val bidLoader = PubMaticBidLoader(request, null)
            loader.setAdLoaderListener(bidLoader)
            loader.loadAd(it, biddingHost)
            bidLoader
        }

    private fun requireBidResponse(request: MediationAdUnitRequest): String? {
        request.bidResponse?.let { return it }
        request.onFailure(
            AdError(AdErrorCode.CONFIGURATION_ERROR, "Supported Biding only")
        )
        return null
    }

}

