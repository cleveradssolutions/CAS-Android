package com.cleveradssolutions.adapters

import com.bytedance.sdk.openadsdk.activity.single.TTLandingPageActivity
import com.bytedance.sdk.openadsdk.api.PAGConstant.PAGPAConsentType
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAd
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerRequest
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeRequest
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenRequest
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAd
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedRequest
import com.cleveradssolutions.adapters.pangle.AppOpenBidLoader
import com.cleveradssolutions.adapters.pangle.AppOpenLoadListener
import com.cleveradssolutions.adapters.pangle.BannerBidLoader
import com.cleveradssolutions.adapters.pangle.BannerLoadListener
import com.cleveradssolutions.adapters.pangle.BuildConfig
import com.cleveradssolutions.adapters.pangle.InterstitialBidLoader
import com.cleveradssolutions.adapters.pangle.InterstitialLoadListener
import com.cleveradssolutions.adapters.pangle.KEY_PAG_ADX
import com.cleveradssolutions.adapters.pangle.KEY_PAG_USER_DATA
import com.cleveradssolutions.adapters.pangle.NativeBidLoader
import com.cleveradssolutions.adapters.pangle.NativeLoadListener
import com.cleveradssolutions.adapters.pangle.RewardedBidLoader
import com.cleveradssolutions.adapters.pangle.RewardedLoadListener
import com.cleveradssolutions.adapters.pangle.SignalCollector
import com.cleveradssolutions.adapters.pangle.applyPAGConfig
import com.cleveradssolutions.adapters.pangle.getPAGSize
import com.cleveradssolutions.adapters.pangle.toCASError
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
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS

// Bidding doc:
// https://bytedance.sg.feishu.cn/docx/doxlgtas4E8VYyO4sl35B7MLyVf

internal class PangleAdapter : MediationRtbAdapter(), PAGSdk.PAGInitCallback {
    override fun getSDKVersion(): String = PAGSdk.getSDKVersion()
    override fun getMinSDKVersion(): String = com.bytedance.sdk.openadsdk.BuildConfig.VERSION_NAME
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = TTLandingPageActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.allFormats or
            AdapterSupport.cas or
            AdapterSupport.max or
            AdapterSupport.iron or
            AdapterSupport.admob

    override val isInitialized: Boolean
        get() = PAGSdk.isInitSuccess()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        if (request.privacy.isAppliesCOPPA() == true) {
            request.onFailure(AdError(AdErrorCode.REJECTED, "Blocked under COPPA restrictions"))
            return
        }

        val context = request.context
        val config = PAGConfig.Builder()
            .appId(appID)
            .debugLog(request.isDebugLogsEnabled)

        request.privacy.isOptOutSaleUS()?.let {
            if (it)
                config.setPAConsent(PAGPAConsentType.PAG_PA_CONSENT_TYPE_NO_CONSENT)
            else
                config.setPAConsent(PAGPAConsentType.PAG_PA_CONSENT_TYPE_CONSENT)
        }

        // resources not found, if you use aab please call PAGConfig.setPackageName
        if (context.packageName != request.casId) {
            config.setPackageName(request.casId)
        }

        request.getStrParameter(KEY_PAG_USER_DATA)?.let {
            config.setUserData(it)
        }
        request.getStrParameter(KEY_PAG_ADX)?.let {
            config.setAdxId(it)
        }
        PAGSdk.init(request.context, config.build(), this)
    }

    override fun success() {
        initRequest.onSuccess()
    }

    override fun fail(code: Int, error: String?) {
        initRequest.onFailure(toCASError(code, error))
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        SignalCollector(request).collect()
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN)
            return super.loadAd(request)

        request.applyPAGConfig()
        val paRequest = PAGBannerRequest(request.getPAGSize())
        paRequest.adString = request.bidResponse
        val listener = if (request is MediationAdBidRequest)
            BannerBidLoader(request)
        else
            BannerLoadListener(request)
        PAGBannerAd.loadAd(request.unitId, paRequest, listener)
        return null
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        request.applyPAGConfig()
        val paRequest = PAGInterstitialRequest()
        paRequest.adString = request.bidResponse
        val listener = if (request is MediationAdBidRequest)
            InterstitialBidLoader(request)
        else
            InterstitialLoadListener(request)
        PAGInterstitialAd.loadAd(request.unitId, paRequest, listener)
        return null
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        request.applyPAGConfig()
        val paRequest = PAGRewardedRequest()
        paRequest.adString = request.bidResponse
        CAS.targetingOptions.userID?.let {
            val extraInfo = HashMap<String?, Any?>()
            extraInfo["user_id"] = it
            paRequest.extraInfo = extraInfo
        }
        val listener = if (request is MediationAdBidRequest)
            RewardedBidLoader(request)
        else
            RewardedLoadListener(request)
        PAGRewardedAd.loadAd(request.unitId, paRequest, listener)
        return null
    }

    override fun loadAd(request: MediationAppOpenAdRequest): MediationAdLoader? {
        request.applyPAGConfig()

        // Wired but should App Icon be refreshed before each loadAd
        val appIconId: Int = request.context.applicationInfo.icon
        if (appIconId > 0) {
            PAGConfig.setAppIconId(appIconId)
        }

        val paRequest = PAGAppOpenRequest()
        paRequest.timeout = 8000 // load timeout
        paRequest.adString = request.bidResponse
        val listener = if (request is MediationAdBidRequest)
            AppOpenBidLoader(request)
        else
            AppOpenLoadListener(request)
        PAGAppOpenAd.loadAd(request.unitId, paRequest, listener)
        return null
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        request.applyPAGConfig()
        val paRequest = PAGNativeRequest()
        paRequest.adString = request.bidResponse
        val listener = if (request is MediationAdBidRequest)
            NativeBidLoader(request)
        else
            NativeLoadListener(request)
        PAGNativeAd.loadAd(request.unitId, paRequest, listener)
        return null
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? =
        if (request.format.isAdView)
            loadAd(request as MediationBannerAdRequest)
        else when (request.format.value) {
            AdFormatId.APP_OPEN -> loadAd(request as MediationAppOpenAdRequest)
            AdFormatId.INTERSTITIAL -> loadAd(request as MediationInterstitialAdRequest)
            AdFormatId.REWARDED -> loadAd(request as MediationRewardedAdRequest)
            AdFormatId.NATIVE -> loadAd(request as MediationNativeAdRequest)
            else -> super.fetchAdBid(request)
        }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.isOptOutSaleUS()?.let {
            PAGConfig.setPAConsent(
                if (it)
                    PAGPAConsentType.PAG_PA_CONSENT_TYPE_NO_CONSENT
                else
                    PAGPAConsentType.PAG_PA_CONSENT_TYPE_CONSENT
            )
        }
    }
}