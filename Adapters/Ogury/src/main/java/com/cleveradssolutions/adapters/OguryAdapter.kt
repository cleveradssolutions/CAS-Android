package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.ogury.BuildConfig
import com.cleveradssolutions.adapters.ogury.OBannerAd
import com.cleveradssolutions.adapters.ogury.OInterstitialAd
import com.cleveradssolutions.adapters.ogury.ORewardedAd
import com.cleveradssolutions.adapters.ogury.OSignalCollector
import com.cleveradssolutions.adapters.ogury.toCASError
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
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.ogury.ad.OguryBidTokenProvider
import com.ogury.ad.common.OguryMediation
import com.ogury.core.OguryError
import com.ogury.core.OguryLog
import com.ogury.sdk.Ogury
import com.ogury.sdk.OguryChildPrivacyTreatment
import com.ogury.sdk.OguryOnStartListener

class OguryAdapter : MediationAdapterBase(), OguryOnStartListener {
    private val mediation = OguryMediation("CAS", CAS.getSDKVersion())
    private val privacyLock = Any()

    override fun getSDKVersion(): String = Ogury.getSdkVersion()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> =
        com.ogury.ad.interstitial.ui.InterstitialActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.banner or
            AdapterSupport.mrec or
            AdapterSupport.inter or
            AdapterSupport.reward or
            AdapterSupport.iron or
            AdapterSupport.max

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return
        if (appID.length != 16) {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "App Id is invalid"))
            return
        }

        OguryLog.enable(if (request.isDebugLogsEnabled) OguryLog.Level.DEBUG else OguryLog.Level.ERROR)
        onUserPrivacyChanged(request.privacy)

        Ogury.start(request.context, appID, this)
    }

    override fun onFailed(error: OguryError) {
        initRequest.onFailure(error.toCASError())
    }

    override fun onStarted() {
        initRequest.onSuccess()
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        OguryBidTokenProvider.getBidToken(request.context, OSignalCollector(request))
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSize.height < 50) {
            return super.loadAd(request)
        }
        return OBannerAd(request, getMediation(request))
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return OInterstitialAd(request, getMediation(request))
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        return ORewardedAd(request, getMediation(request))
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        // Can throw java.util.ConcurrentModificationException

        synchronized(privacyLock) {
            Ogury.applyChildPrivacy(
                privacy.isAppliesCOPPA()?.let {
                    if (privacy.isAppliesGDPR()) {
                        if (it) OguryChildPrivacyTreatment.UNDER_AGE_OF_GDPR_CONSENT_TREATMENT_TRUE
                        else OguryChildPrivacyTreatment.UNDER_AGE_OF_GDPR_CONSENT_TREATMENT_FALSE
                    } else {
                        if (it) OguryChildPrivacyTreatment.CHILD_UNDER_COPPA_TREATMENT_TRUE
                        else OguryChildPrivacyTreatment.CHILD_UNDER_COPPA_TREATMENT_FALSE
                    }
                } ?: OguryChildPrivacyTreatment.UNSPECIFIED
            )
        }

        // The Ogury SDK will automatically synchronize the user's
        // IAB CTF v2 consent before each ad request.
    }

    private fun getMediation(request: MediationAdUnitRequest): OguryMediation {
        request.getStrParameter(Constants.KEY_MEDIATION_NAME)?.let { name ->
            request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER)?.let { version ->
                return OguryMediation(name, version)
            }
        }
        return mediation
    }
}