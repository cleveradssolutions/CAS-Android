package com.cleveradssolutions.adapters

import androidx.annotation.WorkerThread
import com.cleveradssolutions.adapters.bidmachine.BMBannerAd
import com.cleveradssolutions.adapters.bidmachine.BMBannerBidLoader
import com.cleveradssolutions.adapters.bidmachine.BMInterstitialAd
import com.cleveradssolutions.adapters.bidmachine.BMInterstitialBidLoader
import com.cleveradssolutions.adapters.bidmachine.BMNativeAd
import com.cleveradssolutions.adapters.bidmachine.BMNativeBidLoader
import com.cleveradssolutions.adapters.bidmachine.BMRewardedAd
import com.cleveradssolutions.adapters.bidmachine.BMRewardedBidLoader
import com.cleveradssolutions.adapters.bidmachine.BuildConfig
import com.cleveradssolutions.adapters.bidmachine.bannerSize
import com.cleveradssolutions.internal.AdFormatId
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import io.bidmachine.AdPlacementConfig
import io.bidmachine.BidMachine
import io.bidmachine.InitializationCallback
import io.bidmachine.MediaAssetType
import io.bidmachine.TargetingParams
import io.bidmachine.nativead.view.VideoPlayerActivity
import io.bidmachine.utils.Gender
import java.util.Calendar

class BidMachineAdapter : MediationRtbAdapter(), InitializationCallback {

    override fun getSDKVersion(): String = getConstValue(BidMachine::class.java, "VERSION")
    override fun getMinSDKVersion(): String = BidMachine.VERSION
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = VideoPlayerActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.nativ

    override val isInitialized: Boolean
        get() = BidMachine.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val sourceId = request.requireAppID() ?: return

        BidMachine.setLoggingEnabled(request.isDebugLogsEnabled)
        if (request.isTestRequest) {
            BidMachine.setTestMode(true)
        }

        // BidMachine applies the privacy flags of the session it was initialized in,
        // so they are set before initialize() and refreshed on every change.
        onUserPrivacyChanged(request.privacy)

        BidMachine.setTargetingParams(buildUserTargeting())
        BidMachine.initialize(request.context, sourceId, this)
    }

    override fun onInitialized() {
        initRequest.onSuccess()
    }

    private fun buildUserTargeting(): TargetingParams {
        val options = CAS.targetingOptions
        val result = TargetingParams()
        result.setUserId(options.userID)

        if (options.gender != TargetingOptions.GENDER_UNKNOWN) {
            result.setGender(
                if (options.gender == TargetingOptions.GENDER_MALE) Gender.Male
                else Gender.Female
            )
        }
        if (options.age > 0) {
            result.setBirthdayYear(Calendar.getInstance().get(Calendar.YEAR) - options.age)
        }
        options.location?.let {
            result.setDeviceLocation(it)
        }
        options.keywords?.let {
            result.setKeywords(*it.toTypedArray())
        }
        return result
    }

    @WorkerThread
    override fun collectSignals(request: MediationAdSignalRequest) {
        val configBuilder = when {
            request.format.isAdView ->
                AdPlacementConfig.bannerBuilder(request.forBannerAd().bannerSize())

            else -> when (request.format.value) {
                AdFormatId.INTERSTITIAL -> AdPlacementConfig.interstitialBuilder()
                AdFormatId.REWARDED -> AdPlacementConfig.rewardedBuilder()
                AdFormatId.NATIVE -> AdPlacementConfig.nativeBuilder(MediaAssetType.DEFAULT)
                else -> null
            }
        }
        if (configBuilder == null) {
            request.onFailure(AdError("${request.format} not supported"))
            return
        }
        if (request.unitId.isNotEmpty()) {
            configBuilder.withPlacementId(request.unitId)
        }
        val token = BidMachine.getBidToken(request.context, configBuilder.build())
        request.onSuccess(token)
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        val loader = if (request.format.isAdView) {
            val bannerRequest = request.forBannerAd()
            if (bannerRequest.adSizeId == AdSizeId.THIN) {
                // Not supported banner size
                return super.fetchAdBid(request)
            }
            BMBannerBidLoader()
        } else when (request.format) {
            AdFormat.INTERSTITIAL -> BMInterstitialBidLoader()
            AdFormat.REWARDED -> BMRewardedBidLoader()
            AdFormat.NATIVE -> BMNativeBidLoader()
            else -> {
                // Not supported format
                return super.fetchAdBid(request)
            }
        }
        loader.fetchBid(request)
        return loader
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? =
        if (request.adSizeId == AdSizeId.THIN) {
            // Not supported banner size
            super.loadAd(request)
        } else {
            BMBannerAd(request).also { it.load(request) }
        }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader =
        BMInterstitialAd(request).also { it.load(request) }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader =
        BMRewardedAd(request).also { it.load(request) }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader =
        BMNativeAd(request).also { it.load(request) }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.isAppliesCOPPA()?.let {
            BidMachine.setCoppa(it)
        }

        // The Android SDK reads the TCF string from the shared preferences itself,
        // only the consent decision is passed here.
        BidMachine.setSubjectToGDPR(privacy.isAppliesGDPR())
        privacy.hasConsent(AdSourceId.BIDMACHINE)?.let {
            BidMachine.setConsentStatus(it)
        }
    }
}
