package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.moloco.BuildConfig
import com.cleveradssolutions.adapters.moloco.InitializationListener
import com.cleveradssolutions.adapters.moloco.MolocoBannerAd
import com.cleveradssolutions.adapters.moloco.MolocoNativeAdLoader
import com.cleveradssolutions.adapters.moloco.MolocoScreenAd
import com.cleveradssolutions.adapters.moloco.SignalListener
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.BannerAdSize
import com.moloco.sdk.publisher.InterstitialAd
import com.moloco.sdk.publisher.InterstitialAdShowListener
import com.moloco.sdk.publisher.MediationInfo
import com.moloco.sdk.publisher.Moloco
import com.moloco.sdk.publisher.RewardedInterstitialAd
import com.moloco.sdk.publisher.RewardedInterstitialAdShowListener
import com.moloco.sdk.publisher.init.MolocoInitParams
import com.moloco.sdk.publisher.privacy.MolocoPrivacy

class MolocoAdapter : MediationAdapterBase() {
    private var mediationInfo = MediationInfo("CAS")

    override fun getSDKVersion(): String =
        getConstValue(com.moloco.sdk.BuildConfig::class.java, "SDK_VERSION_NAME")

    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK

    override fun getAdapterVersion(): String = BuildConfig.VERSION

    override fun getActivityClass(): Class<*> = Moloco::class.java

    override fun supportBidding(): Int =
        AdapterSupport.max or
                AdapterSupport.iron or
                AdapterSupport.admob or
                AdapterSupport.banner or
                AdapterSupport.mrec or
                AdapterSupport.inter or
                AdapterSupport.reward or
                AdapterSupport.nativ

    override fun initAds(request: MediationInitAdRequest) {
        val appKey = request.requireAppID() ?: return

        request.getStrParameter(Constants.KEY_MEDIATION_NAME)?.let {
            mediationInfo = MediationInfo(it)
        }
        onUserPrivacyChanged(request.privacy)

        val params = MolocoInitParams(
            appContext = request.context,
            appKey = appKey,
            mediationInfo = mediationInfo
        )

        Moloco.initialize(params, InitializationListener(request))
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        Moloco.getBidToken(mediationInfo, request.context, SignalListener(request))
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        val adSize = request.adSize
        val bidResponse = request.requireBidResponse() ?: return null
        val bannerSize = when {
            request.adSizeId == AdSizeId.THIN -> return super.loadAd(request)
            request.adSizeId == AdSizeId.MREC -> BannerAdSize.MREC
            adSize.isAdaptive -> BannerAdSize.AnchoredAdaptive(adSize.width)
            adSize.isInline -> BannerAdSize.InlineAdaptive(adSize.width)
            request.adSizeId == AdSizeId.LARGE -> BannerAdSize.Tablet
            else -> BannerAdSize.Standard
        }

        val loader = MolocoBannerAd(request, bidResponse)
        Moloco.createMolocoBanner(
            mediationInfo, request.unitId, bannerSize, null, loader
        )
        return loader
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        val bidResponse = request.requireBidResponse() ?: return null
        val loader =
            MolocoScreenAd<InterstitialAdShowListener, InterstitialAd>(request, bidResponse)
        Moloco.createInterstitial(mediationInfo, request.unitId, null, loader)
        return loader
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        val bidResponse = request.requireBidResponse() ?: return null
        val loader = MolocoScreenAd<RewardedInterstitialAdShowListener, RewardedInterstitialAd>(
            request,
            bidResponse
        )
        Moloco.createRewardedInterstitial(mediationInfo, request.unitId, null, loader)
        return loader
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        val bidResponse = request.requireBidResponse() ?: return null
        val loader = MolocoNativeAdLoader(request, bidResponse)
        Moloco.createNativeAd(mediationInfo, request.unitId, null, loader)
        return loader
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        MolocoPrivacy.setPrivacy(
            MolocoPrivacy.PrivacySettings(
                isUserConsent = privacy.hasConsent(AdSourceId.MOLOCO),
                isAgeRestrictedUser = privacy.isAppliesCOPPA(),
                isDoNotSell = privacy.isOptOutSaleUS(),
            )
        )
    }

    private fun MediationAdUnitRequest.requireBidResponse(): String? {
        bidResponse?.let { return it }
        onFailure(
            AdError(
                AdErrorCode.CONFIGURATION_ERROR,
                "Moloco supports bidding load only"
            )
        )
        return null
    }
}
