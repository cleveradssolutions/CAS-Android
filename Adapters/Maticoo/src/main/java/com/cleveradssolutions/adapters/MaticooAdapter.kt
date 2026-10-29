package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.maticoo.BuildConfig
import com.cleveradssolutions.adapters.maticoo.MaticooBanner
import com.cleveradssolutions.adapters.maticoo.MaticooInterstitialAd
import com.cleveradssolutions.adapters.maticoo.MaticooNativeAdLoader
import com.cleveradssolutions.adapters.maticoo.MaticooRewardedAd
import com.cleveradssolutions.adapters.maticoo.toCASError
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.maticoo.sdk.InitConfiguration
import com.maticoo.sdk.core.InitCallback
import com.maticoo.sdk.core.MaticooAds
import com.zmaticoo.sdk.base.common.ZMaticooAds
import com.zmaticoo.sdk.flow.model.ComponentError

class MaticooAdapter : MediationAdapterBase(), InitCallback {
    private val localExtras = mutableMapOf<String, Any>("source" to "cas.ai")

    override fun getSDKVersion(): String = ZMaticooAds.getSDKVersion()
    override fun getMinSDKVersion(): String = ZMaticooAds.SDK_VERSION
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> =
        com.zmaticoo.sdk.flow.components.AdActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.nativ

    override val isInitialized: Boolean
        get() = ZMaticooAds.isSdkInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appKey = request.requireAppID() ?: return

        if (appKey.length != 64) {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "Invalid AppId"))
            return
        }

        onUserPrivacyChanged(request.privacy)

        val configuration = InitConfiguration.Builder()
            .appKey(appKey)
            .logEnable(request.isDebugLogsEnabled)
            .localExtra(localExtras)
            .build()
        MaticooAds.init(
            request.contextService.getActivityOrNull(),
            configuration,
            this
        )
        //ZMaticooAds.initializeSdk(request.context, configuration, this)
        onMuteAdSoundsChanged(CAS.settings.mutedAdSounds)
    }

    override fun onSuccess() {
        initRequest.onSuccess()
    }

    override fun onError(error: ComponentError) {
        initRequest.onFailure(error.toCASError())
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        // Unit ID must be integer only
        if (request.unitId.toLongOrNull() == null) {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "Invalid AdUnit id"))
            return
        }

        request.onSuccess(
            ZMaticooAds.getBiddingToken(request.unitId, request.timestamp)
                .orEmpty()
        )
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSize.height < 50) {
            return super.loadAd(request)
        }
        return MaticooBanner(request, localExtras)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader {
        return MaticooInterstitialAd(request, localExtras)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader {
        return MaticooRewardedAd(request, localExtras)
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader {
        return MaticooNativeAdLoader(request, localExtras)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        // true means that user data can be obtained. The default value is true.
        privacy.hasConsent(AdSourceId.MATICOO)?.let {
            MaticooAds.setGDPRConsent(it)
        }

        // true means no information is collected, the default value is false
        privacy.isOptOutSaleUS()?.let {
            MaticooAds.setDoNotSell(it)
        }

        privacy.isAppliesCOPPA()?.let {
            MaticooAds.setCoppa(initRequest.context, if (it) 1 else 0)
        }
    }

    override fun onMuteAdSoundsChanged(muted: Boolean) {
        localExtras["is_muted"] = muted
    }
}