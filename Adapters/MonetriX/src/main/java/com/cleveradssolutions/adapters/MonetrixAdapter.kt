package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.monetrix.BuildConfig
import com.cleveradssolutions.adapters.monetrix.MXLoader
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.monetrix.adsdk.Monetrix
import com.monetrix.adsdk.api.banner.BannerAd
import com.monetrix.adsdk.api.banner.BannerAdLoad
import com.monetrix.adsdk.api.banner.BannerAdLoader
import com.monetrix.adsdk.api.base.ContainerActivity
import com.monetrix.adsdk.api.base.InitConfig
import com.monetrix.adsdk.api.interstitial.InstlAd
import com.monetrix.adsdk.api.interstitial.InstlAdLoad
import com.monetrix.adsdk.api.interstitial.InstlAdLoader
import com.monetrix.adsdk.api.nativead.NativeAd
import com.monetrix.adsdk.api.nativead.NativeAdLoad
import com.monetrix.adsdk.api.nativead.NativeAdLoader
import com.monetrix.adsdk.api.reward.RwdAd
import com.monetrix.adsdk.api.reward.RwdAdLoad
import com.monetrix.adsdk.api.reward.RwdAdLoader

// docs: https://docs.google.com/document/d/1vwvV9Fr-rv6SpFtbGHt3CzbVB_SjI88GgQwlnrXCAwo/edit?tab=t.avvqxj9bjp94

class MonetrixAdapter : MediationRtbAdapter(), Monetrix.InitListener {

    override fun getSDKVersion(): String = Monetrix.getSDKVersionName()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = ContainerActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.nativ

    override val isInitialized: Boolean
        get() = Monetrix.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        if (request.privacy.isAppliesCOPPA() == true) {
            // If your app is for children under 13, you must not use MonetriX SDK for monetization.
            request.onFailure(
                AdError(
                    AdErrorCode.REJECTED,
                    "Blocked under COPPA restrictions"
                )
            )
            return
        }

        val config = InitConfig.Builder()
            .setAppId(appID)
            .build()

        onUserPrivacyChanged(request.privacy)

        Monetrix.initialize(request.context, config, this)
    }

    override fun onInitSuccess() {
        initRequest.onSuccess()
    }

    override fun onInitFail(code: Int, message: String?) {
        initRequest.onFailure(AdError("$message (c$code)"))
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        val context = initRequest.context
        privacy.hasConsent(AdSourceId.UNKNOWN)?.let {
            Monetrix.setGDPRFlag(context, it)
        }

        privacy.isOptOutSaleUS()?.let {
            val optIn = !it
            Monetrix.setCCPAFlag(context, optIn)
        }
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? =
        if (request.format.isAdView) {
            loadAd(request as MediationBannerAdRequest)
        } else when (request.format.value) {
            AdFormat.INTERSTITIAL.value -> loadAd(request as MediationInterstitialAdRequest)
            AdFormat.REWARDED.value -> loadAd(request as MediationRewardedAdRequest)
            AdFormat.NATIVE.value -> loadAd(request as MediationNativeAdRequest)
            else -> super.fetchAdBid(request)
        }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        // Separated placement ID created for each ad size.
        if (request.adSizeId == AdSizeId.THIN) {
            return super.loadAd(request)
        }
        val loader = MXLoader<BannerAd>(request)
        val params = BannerAdLoad.Builder()
            .withPlacementId(request.unitId)
            .build()
        BannerAdLoader.Builder()
            .withAdLoadCallback(loader)
            .build()
            .loadAd(params)
        return loader
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader {
        val loader = MXLoader<InstlAd>(request)
        val params = InstlAdLoad.Builder()
            .withPlacementId(request.unitId)
            .build()
        InstlAdLoader.Builder()
            .withAdLoadCallback(loader)
            .build()
            .loadAd(params)
        return loader
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader {
        val loader = MXLoader<RwdAd>(request)
        val params = RwdAdLoad.Builder()
            .withPlacementId(request.unitId)
            .build()

        RwdAdLoader.Builder()
            .withAdLoadCallback(loader)
            .build()
            .loadAd(params)
        return loader
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        val loader = MXLoader<NativeAd>(request)

        val params = NativeAdLoad.Builder()
            .withPlacementId(request.unitId)
            .build()

        NativeAdLoader.Builder()
            .withAdLoadCallback(loader)
            .build()
            .loadAd(params)
        return loader
    }
}