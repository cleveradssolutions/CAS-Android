package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.startio.BuildConfig
import com.cleveradssolutions.adapters.startio.SIONativeAdLoader
import com.cleveradssolutions.adapters.startio.StartIOBannerAd
import com.cleveradssolutions.adapters.startio.StartIOScreenAd
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import com.startapp.sdk.ads.interstitials.OverlayActivity
import com.startapp.sdk.adsbase.SDKAdPreferences
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK


class StartIOAdapter : MediationAdapterBase(), Runnable {

    override fun getSDKVersion(): String = StartAppSDK.getVersion()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = OverlayActivity::class.java

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        val context = request.context
        val targeting = CAS.targetingOptions
        val preferences = SDKAdPreferences()
        if (targeting.age != 0)
            preferences.setAge(targeting.age)
        if (targeting.gender == TargetingOptions.GENDER_MALE)
            preferences.gender = SDKAdPreferences.Gender.MALE
        else if (targeting.gender == TargetingOptions.GENDER_FEMALE)
            preferences.gender = SDKAdPreferences.Gender.FEMALE

        StartAppSDK.enableMediationMode(context, "CAS", getAdapterVersion())

        StartAppSDK.initParams(context, appID)
            .setSdkAdPrefs(preferences)
            //.setReturnAdsEnabled(false) deprecated
            .setCallback(this)
            .init()

        if (request.isTestRequest)
            StartAppSDK.setTestAdsEnabled(true)

        onUserPrivacyChanged(request.privacy)
    }

    override fun run() {
        initRequest.onSuccess()
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        return if (request.adSize.height < 50)
            super.loadAd(request)
        else
            StartIOBannerAd()
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return StartIOScreenAd(request, StartAppAd.AdMode.AUTOMATIC)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        return StartIOScreenAd(request, StartAppAd.AdMode.REWARDED_VIDEO)
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        return SIONativeAdLoader(request)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
//        privacy.hasConsentGDPR(AdNetwork.STARTIO)?.let {
//            StartAppSDK.setUserConsent(
//                contextService.getContext(),
//                "pas",
//                System.currentTimeMillis(),
//                it
//            )
//        }
        try {
            val extras = StartAppSDK.getExtras(initRequest.context).edit()
            //extras.putString("IABUSPrivacy_String", privacy.getUSPrivacy(AdNetwork.STARTIO))

            privacy.hasConsent(AdSourceId.STARTIO)?.let {
                extras.putBoolean("medPas", it)
            }

            privacy.isOptOutSaleUS()?.let {
                extras.putBoolean("medCCPA", it)
            }

            privacy.isAppliesCOPPA()?.let {
                extras.putBoolean("medAgeRestrict", it)
            }

            extras.apply()
        } catch (e: Throwable) {
            debug("onUserPrivacyChanged", e)
        }
    }

}