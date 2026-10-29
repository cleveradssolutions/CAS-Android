package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.bigo.BigoAppOpenLoader
import com.cleveradssolutions.adapters.bigo.BigoBannerLoader
import com.cleveradssolutions.adapters.bigo.BigoInterstitialLoader
import com.cleveradssolutions.adapters.bigo.BigoNativeLoader
import com.cleveradssolutions.adapters.bigo.BigoRewardedLoader
import com.cleveradssolutions.adapters.bigo.BuildConfig
import com.cleveradssolutions.internal.AdFormatId
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAppOpenAdRequest
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
import sg.bigo.ads.BigoAdSdk
import sg.bigo.ads.ConsentOptions
import sg.bigo.ads.api.AdActivity
import sg.bigo.ads.api.AdConfig

class BigoAdapter : MediationRtbAdapter(), BigoAdSdk.InitListener {
    override fun getSDKVersion(): String = BigoAdSdk.getSDKVersionName()
    override fun getMinSDKVersion(): String? = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = AdActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.cas or
            AdapterSupport.max or
            AdapterSupport.iron or
            AdapterSupport.allFormats

    override val isInitialized: Boolean
        get() = BigoAdSdk.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return
        val application = request.context

        onUserPrivacyChanged(request.privacy)

        val config = AdConfig.Builder()
            .setAppId(appID)
            .setDebug(request.isDebugLogsEnabled)

        // Set your app's distribution channel.
        // This will help us to optimize the efficiency of advertising monetization by channel.
        //config.setChannel("<your-app-channel>")
        //config.setActivatedTime("<your-app-activated-timestamp>")

        if (CAS.targetingOptions.age > 0)
            config.setAge(CAS.targetingOptions.age)
        if (CAS.targetingOptions.gender == TargetingOptions.GENDER_MALE)
            config.setGender(AdConfig.GENDER_MALE)
        else if (CAS.targetingOptions.gender == TargetingOptions.GENDER_FEMALE)
            config.setGender(AdConfig.GENDER_FEMALE)

        BigoAdSdk.initialize(application, config.build(), this)
    }

    override fun onInitialized() {
        // Should be check BigoAdSdk.isInitialized() to detect init failed.
        // It is do internally in CAS Core
        initRequest.onSuccess()
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val token = BigoAdSdk.getBidderToken()
        if (token.isNullOrEmpty()) {
            request.onFailure(AdError.NOT_INITIALIZED)
        } else {
            request.onSuccess(token)
        }
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN)
            return super.loadAd(request)
        return BigoBannerLoader(request)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return BigoInterstitialLoader(request)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        return BigoRewardedLoader(request)
    }

    override fun loadAd(request: MediationAppOpenAdRequest): MediationAdLoader? {
        return BigoAppOpenLoader(request)
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        return BigoNativeLoader(request)
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        if (request.format.isAdView) {
            if (request.forBannerAd().adSizeId == AdSizeId.THIN) {
                return super.fetchAdBid(request)
            }
            return BigoBannerLoader(request.forBannerAd())
        }
        return when (request.format.value) {
            AdFormatId.APP_OPEN -> BigoAppOpenLoader(request.forScreenAd())
            AdFormatId.INTERSTITIAL -> BigoInterstitialLoader(request.forScreenAd())
            AdFormatId.REWARDED -> BigoRewardedLoader(request.forScreenAd())
            AdFormatId.NATIVE -> BigoNativeLoader(request.forNativeAd())
            else -> super.fetchAdBid(request)
        }
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        val context = initRequest.context
        privacy.hasConsent(AdSourceId.BIGO)?.let {
            if (privacy.isAppliesGDPR()) {
                BigoAdSdk.setUserConsent(context, ConsentOptions.GDPR, it)
            }
        }

        privacy.isOptOutSaleUS()?.let {
            // A value of "true" implies the user has given consent to store
            // and process personal information and a value of "false" means
            // the user has declined consent.
            val optIn = !it
            BigoAdSdk.setUserConsent(context, ConsentOptions.CCPA, optIn)
        }

        // A value of "true" indicates that the user is not a child under 13 years old,
        // and a value of "false" indicates that the user is a child under 13 years old.
        privacy.isAppliesCOPPA()?.let {
            val isNotChildAudience = !it
            BigoAdSdk.setUserConsent(context, ConsentOptions.COPPA, isNotChildAudience)
        }
    }
}