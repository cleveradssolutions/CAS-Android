package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.verve.BuildConfig
import com.cleveradssolutions.adapters.verve.VerveBannerAd
import com.cleveradssolutions.adapters.verve.VerveInterstitialAd
import com.cleveradssolutions.adapters.verve.VerveRewardedAd
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.interstitial.activity.HyBidInterstitialActivity
import net.pubnative.lite.sdk.utils.Logger

class VerveAdapter : MediationAdapterBase(), HyBid.InitialisationListener {

    override fun getSDKVersion(): String = HyBid.HYBID_VERSION
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = HyBidInterstitialActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.banner or
            AdapterSupport.mrec or
            AdapterSupport.inter or
            AdapterSupport.reward or
            AdapterSupport.nativ or
            AdapterSupport.iron or
            AdapterSupport.max

    override val isInitialized: Boolean
        get() = HyBid.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appToken = request.requireAppID() ?: return

        if (request.isTestRequest)
            HyBid.setTestMode(true)

        if (request.isDebugLogsEnabled)
            HyBid.setLogLevel(Logger.Level.debug)

        onUserPrivacyChanged(request.privacy)

        if (CAS.targetingOptions.age > 0) {
            HyBid.setAge(CAS.targetingOptions.age.toString())
        }
        if (CAS.targetingOptions.gender != TargetingOptions.GENDER_UNKNOWN) {
            HyBid.setGender(
                if (CAS.targetingOptions.gender == TargetingOptions.GENDER_MALE) "m"
                else "f"
            )
        }
        request.targeting.appVersion?.let {
            HyBid.setAppVersion(it)
        }

        HyBid.initialize(appToken, initRequest.context, this)
    }

    override fun onInitialisationFinished(success: Boolean) {
        if (success)
            initRequest.onSuccess()
        else
            initRequest.onFailure(AdError(AdErrorCode.NOT_INITIALIZED))
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val signal = HyBid.getCustomRequestSignalData()
        request.onSuccess(signal)
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN)
            return super.loadAd(request)
        return VerveBannerAd(request)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader {
        return VerveInterstitialAd(request)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader {
        return VerveRewardedAd(request)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        val userDataManager = HyBid.getUserDataManager()

        val hasConsent = privacy.hasConsent(AdSourceId.VERVE)
        if (hasConsent != null && userDataManager != null) {
            val verveGDPRConsentString = userDataManager.iabgdprConsentString
            if (!hasConsent) {
                userDataManager.iabgdprConsentString = "0"
            } else if (verveGDPRConsentString.isNullOrEmpty() || verveGDPRConsentString == "0") {
                userDataManager.iabgdprConsentString = "1"
            }
        }

        if (userDataManager != null && userDataManager.iabusPrivacyString.isNullOrEmpty()) {
            val isOptOut = privacy.isOptOutSaleUS()
            if (isOptOut == true) {
                userDataManager.iabusPrivacyString = "1NYN"
            } else {
                val usPrivacyString = privacy.getUSPrivacyString()
                if (usPrivacyString.isNotEmpty()) {
                    userDataManager.iabusPrivacyString = usPrivacyString
                }
            }
        }

        val isCoppa = privacy.isAppliesCOPPA()
        if (isCoppa != null) {
            HyBid.setCoppaEnabled(isCoppa)
        }

        HyBid.setLocationUpdatesEnabled(CAS.targetingOptions.locationCollectionEnabled)
    }

}
