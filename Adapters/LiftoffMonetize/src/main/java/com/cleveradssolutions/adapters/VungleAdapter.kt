package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.vungle.BuildConfig
import com.cleveradssolutions.adapters.vungle.LiftoffBannerAd
import com.cleveradssolutions.adapters.vungle.LiftoffInterstitialAd
import com.cleveradssolutions.adapters.vungle.LiftoffNativeLoader
import com.cleveradssolutions.adapters.vungle.LiftoffSignalCollector
import com.cleveradssolutions.adapters.vungle.toCASError
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationAppOpenAdRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.android.CAS
import com.vungle.ads.InitializationListener
import com.vungle.ads.RewardedAd
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleError
import com.vungle.ads.VunglePrivacySettings
import com.vungle.ads.internal.ui.VungleActivity

class VungleAdapter : MediationAdapterBase(), InitializationListener {
    override fun getSDKVersion(): String = VungleAds.getSdkVersion()
    override fun getMinSDKVersion(): String = com.vungle.ads.BuildConfig.VERSION_NAME
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = VungleActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.allFormats or
            AdapterSupport.cas or
            AdapterSupport.max or
            AdapterSupport.iron or
            AdapterSupport.admob

    override val isInitialized: Boolean
        get() = VungleAds.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        request.getStrParameter(Constants.KEY_MEDIATION_ADAPTER_VER)?.let {
            val integrationType = request.getStrParameter(Constants.KEY_MEDIATION_NAME) ?: "vunglehbs"
            VungleAds.setIntegrationName(integrationType, it)
        }

        onUserPrivacyChanged(request.privacy)
        VungleAds.init(request.context, appID, this)
    }

    override fun onSuccess() {
        initRequest.onSuccess()
    }

    override fun onError(vungleError: VungleError) {
        initRequest.onFailure(vungleError.toCASError())
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        // Can be collected before init sdk
        onUserPrivacyChanged(request.privacy)

        LiftoffSignalCollector(request).collect()
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSize.height < 50)
            return super.loadAd(request)
        return LiftoffBannerAd()
    }

    override fun loadAd(request: MediationAppOpenAdRequest): MediationAdLoader? {
        return LiftoffInterstitialAd(request)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return LiftoffInterstitialAd(request)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        val view = RewardedAd(request.context, request.unitId)
        CAS.targetingOptions.userID?.let {
            view.setUserId(it)
        }
        return LiftoffInterstitialAd(view, request)
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        return LiftoffNativeLoader(request)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.isOptOutSaleUS()?.let {
            val optIn = !it
            VunglePrivacySettings.setCCPAStatus(optIn)
        }
        privacy.hasConsent(AdSourceId.LIFTOFF_MONETIZE)?.let {
            VunglePrivacySettings.setGDPRStatus(it, null)
        }
        privacy.isAppliesCOPPA()?.let {
            VunglePrivacySettings.setCOPPAStatus(it)
        }
    }
}