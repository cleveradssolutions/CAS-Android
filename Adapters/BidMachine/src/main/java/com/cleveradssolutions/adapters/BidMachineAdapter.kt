package com.cleveradssolutions.adapters

import androidx.annotation.WorkerThread
import com.cleveradssolutions.adapters.bidmachine.BidMachineBannerAd
import com.cleveradssolutions.adapters.bidmachine.BidMachineBannerBidLoader
import com.cleveradssolutions.adapters.bidmachine.BidMachineInterstitialAd
import com.cleveradssolutions.adapters.bidmachine.BidMachineInterstitialBidLoader
import com.cleveradssolutions.adapters.bidmachine.BidMachineNativeAd
import com.cleveradssolutions.adapters.bidmachine.BidMachineNativeBidLoader
import com.cleveradssolutions.adapters.bidmachine.BidMachineRewardedAd
import com.cleveradssolutions.adapters.bidmachine.BidMachineRewardedBidLoader
import com.cleveradssolutions.adapters.bidmachine.BuildConfig
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineBannerPlacement
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineBannerRequest
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineInterstitialPlacement
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineInterstitialRequest
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineNativePlacement
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineNativeRequest
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineRewardedPlacement
import com.cleveradssolutions.adapters.bidmachine.buildBidMachineRewardedRequest
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
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import io.bidmachine.BidMachine
import io.bidmachine.InitializationCallback
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
        // Assigned, not only raised: the flag outlives the request that set it
        // and would leave the SDK in test mode for the rest of the process.
        BidMachine.setTestMode(request.isTestRequest)

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
        val placement = when {
            request.format.isAdView -> request.forBannerAd().buildBidMachineBannerPlacement()
            else -> when (request.format.value) {
                AdFormatId.INTERSTITIAL -> request.buildBidMachineInterstitialPlacement()
                AdFormatId.REWARDED -> request.buildBidMachineRewardedPlacement()
                AdFormatId.NATIVE -> request.buildBidMachineNativePlacement()
                else -> null
            }
        }
        if (placement == null) {
            request.onFailure(AdError("${request.format} not supported"))
            return
        }
        val token = BidMachine.getBidToken(request.context, placement)
        request.onSuccess(token)
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        val loader = if (request.format.isAdView) {
            val bannerRequest = request.forBannerAd()
            if (bannerRequest.adSizeId == AdSizeId.THIN) {
                // Not supported banner size
                return super.fetchAdBid(request)
            }
            BidMachineBannerBidLoader()
        } else when (request.format.value) {
            AdFormatId.INTERSTITIAL -> BidMachineInterstitialBidLoader()
            AdFormatId.REWARDED -> BidMachineRewardedBidLoader()
            AdFormatId.NATIVE -> BidMachineNativeBidLoader()
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
            BidMachineBannerAd(request).also {
                it.load(request.buildBidMachineBannerRequest().build())
            }
        }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader =
        BidMachineInterstitialAd(request).also {
            it.load(request.buildBidMachineInterstitialRequest().build())
        }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader =
        BidMachineRewardedAd(request).also {
            it.load(request.buildBidMachineRewardedRequest().build())
        }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader =
        BidMachineNativeAd(request).also {
            it.load(request.buildBidMachineNativeRequest().build())
        }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.isAppliesCOPPA()?.let {
            BidMachine.setCoppa(it)
        }
        privacy.hasConsent(AdSourceId.BIDMACHINE)?.let {
            BidMachine.setConsentStatus(it)
        }
    }
}
