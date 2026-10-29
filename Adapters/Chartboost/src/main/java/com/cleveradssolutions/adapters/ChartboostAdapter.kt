package com.cleveradssolutions.adapters

import com.chartboost.sdk.Chartboost
import com.chartboost.sdk.LoggingLevel
import com.chartboost.sdk.Mediation
import com.chartboost.sdk.callbacks.StartCallback
import com.chartboost.sdk.events.StartError
import com.chartboost.sdk.privacy.model.CCPA
import com.chartboost.sdk.privacy.model.CCPA.CCPA_CONSENT
import com.chartboost.sdk.privacy.model.COPPA
import com.chartboost.sdk.privacy.model.GDPR
import com.chartboost.sdk.view.FullscreenAdActivity
import com.cleveradssolutions.adapters.chartboost.BuildConfig
import com.cleveradssolutions.adapters.chartboost.ChartboostAd
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
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS

class ChartboostAdapter : MediationAdapterBase(), StartCallback {
    private val mediation = Mediation("CAS", CAS.getSDKVersion(), getAdapterVersion())

    override fun getSDKVersion(): String = Chartboost.getSDKVersion()
    override fun getMinSDKVersion(): String? = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = FullscreenAdActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or
            AdapterSupport.iron or
            AdapterSupport.admob or
            AdapterSupport.max

    override val isInitialized: Boolean
        get() = Chartboost.isSdkStarted()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return
        if (appID.length != 24) {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "App Id has invalid length"))
            return
        }
        val appSignature = request.getStrParameter("signature")
        if (appSignature.isNullOrEmpty()) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "App Signature not configured"
                )
            )
            return
        }
        if (appSignature.length != 40) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "App Signature has invalid size"
                )
            )
            return
        }

        val context = request.context

        Chartboost.addDataUseConsent(
            context, COPPA(
                request.privacy.isAppliesCOPPA() == true
            )
        )

        // We must update consent _before_ calling Chartboost#startWithAppId()
        onUserPrivacyChanged(request.privacy)

        if (request.isDebugLogsEnabled)
            Chartboost.setLoggingLevel(LoggingLevel.ALL)
        else
            Chartboost.setLoggingLevel(LoggingLevel.NONE)

        Chartboost.startWithAppId(context, appID, appSignature, this)
    }

    override fun onStartCompleted(error: StartError?) {
        if (error == null)
            initRequest.onSuccess()
        else
            initRequest.onFailure(
                when (error.code) {
                    StartError.Code.NETWORK_FAILURE -> AdError.NO_CONNECTION
                    StartError.Code.INVALID_CREDENTIALS ->
                        AdError(AdErrorCode.CONFIGURATION_ERROR, error.toString())

                    else -> AdError(AdErrorCode.INTERNAL_ERROR, error.toString())
                }
            )
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        if (isInitialized) {
            val signal = Chartboost.getBidderToken()
            // Not required value and can be null
            request.onSuccess(signal.orEmpty())
        } else {
            request.onFailure(AdError.NOT_INITIALIZED)
        }
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        return if (request.adSize.height < 50) super.loadAd(request)
        else loadBaseAd(request)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? =
        loadBaseAd(request)

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? =
        loadBaseAd(request)

    private fun loadBaseAd(request: MediationAdUnitRequest): MediationAdLoader? =
        if (!request.isTestRequest && request.unitId == "Default") {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Default location not supported"
                )
            )
            null
        } else {
            ChartboostAd(request, mediation)
        }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        val context = initRequest.context
        privacy.hasConsent(AdSourceId.CHARTBOOST)?.let {
//            Chartboost.addDataUseConsent(
//                context, LGPD(true)
//            )
            Chartboost.addDataUseConsent(
                context, GDPR(
                    if (it) GDPR.GDPR_CONSENT.BEHAVIORAL
                    else GDPR.GDPR_CONSENT.NON_BEHAVIORAL
                )
            )
        } ?: Chartboost.clearDataUseConsent(context, GDPR.GDPR_STANDARD)

        privacy.isOptOutSaleUS()?.let {
            val ccpaConsent = CCPA(if (it) CCPA_CONSENT.OPT_OUT_SALE else CCPA_CONSENT.OPT_IN_SALE)
            Chartboost.addDataUseConsent(context, ccpaConsent)
        }
    }
}