package com.cleveradssolutions.adapters

import android.os.Build
import com.cleveradssolutions.adapters.hyprmx.HMXBannerAd
import com.cleveradssolutions.adapters.hyprmx.HMXScreenAd
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
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.hyprmx.android.BuildConfig
import com.hyprmx.android.sdk.activity.HyprMXRequiredInformationActivity
import com.hyprmx.android.sdk.consent.ConsentStatus
import com.hyprmx.android.sdk.core.HyprMX
import com.hyprmx.android.sdk.core.HyprMXIf
import com.hyprmx.android.sdk.core.HyprMXState
import com.hyprmx.android.sdk.core.InitResult
import com.hyprmx.android.sdk.placement.Placement
import com.hyprmx.android.sdk.placement.PlacementType
import com.hyprmx.android.sdk.utility.HyprMXLog
import com.hyprmx.android.sdk.utility.HyprMXProperties

class HyprMXAdapter : MediationAdapterBase(), HyprMXIf.HyprMXInitializationListener {
    override fun getSDKVersion(): String = getConstValue(HyprMXProperties::class.java, "version")
    override fun getMinSDKVersion(): String = BuildConfig.HYPRMX_VERSION
    override fun getAdapterVersion(): String =
        com.cleveradssolutions.adapters.hyprmx.BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = HyprMXRequiredInformationActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple

    override val isInitialized: Boolean
        get() = HyprMX.getInitializationState() == HyprMXState.INITIALIZATION_COMPLETE

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            request.onFailure(
                AdError(
                    AdErrorCode.REJECTED,
                    "HyprMX requires Android OS version API 28 or newer"
                )
            )
            return
        }

        HyprMXLog.enableDebugLogs(request.isDebugLogsEnabled)
        onUserPrivacyChanged(request.privacy)

        HyprMX.setMediationProvider(
            request.getStrParameter(Constants.KEY_MEDIATION_NAME) ?: "cas_ai",
            request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER) ?: CAS.getSDKVersion(),
            request.getStrParameter(Constants.KEY_MEDIATION_ADAPTER_VER) ?: getAdapterVersion()

        )

        // Test mode server is invalid
//        if (request.isTestRequest) {
//            HyprMX.enableTestMode()
//        }

        HyprMX.initialize(request.context, appID, this)
    }

    override fun onInitialized(result: InitResult) {
        if (result.success) {
            onUserPrivacyChanged(initRequest.privacy)
            initRequest.onSuccess()
        } else {
            initRequest.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, result.message))
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        if (!isInitialized) {
            request.onFailure(AdError.NOT_INITIALIZED)
            return
        }
        val token = HyprMX.sessionToken()
        if (token.isNullOrEmpty()) {
            request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR))
            return
        }
        request.onSuccess(token)
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        return if (request.adSize.height < 50) super.loadAd(request)
        else HMXBannerAd()
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return getPlacement(request, PlacementType.INTERSTITIAL)?.let { HMXScreenAd(it, request) }
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        return getPlacement(request, PlacementType.REWARDED)?.let { HMXScreenAd(it, request) }
    }

    private fun getPlacement(request: MediationAdUnitRequest, type: PlacementType): Placement? {
        val placement = HyprMX.getPlacement(request.unitId)
        return if (placement.type == PlacementType.INVALID) {
            request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, "Placement is invalid"))
            null
        } else if (placement.type != type) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Placement type is not match"
                )
            )
            null
        } else {
            placement
        }
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        // NOTE: HyprMX requested to always set GDPR regardless of region.
        HyprMX.setConsentStatus(getHMXConsent(privacy))

        privacy.isAppliesCOPPA()?.let {
            HyprMX.setAgeRestrictedUser(it)
        }
    }

    private fun getHMXConsent(privacy: MediationUserPrivacy): ConsentStatus {
        val hasUserConsent = privacy.hasConsent(AdSourceId.HYPRMX)
        val isDoNotSell = privacy.isOptOutSaleUS()

        return if ((isDoNotSell == null || isDoNotSell == false) && hasUserConsent == true) {
            ConsentStatus.CONSENT_GIVEN
        } else if (isDoNotSell == true || hasUserConsent == false) {
            ConsentStatus.CONSENT_DECLINED
        } else {
            ConsentStatus.CONSENT_STATUS_UNKNOWN
        }
    }
}