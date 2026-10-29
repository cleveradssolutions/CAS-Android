package com.cleveradssolutions.adapters

import android.annotation.SuppressLint
import com.blueseasx.sdk.AdActivity
import com.blueseasx.sdk.BSXAdView
import com.blueseasx.sdk.BlueseasxSDK
import com.blueseasx.sdk.Consent
import com.blueseasx.sdk.InterstitialAd
import com.blueseasx.sdk.MediationInfo
import com.blueseasx.sdk.RewardVideoAd
import com.blueseasx.sdk.ad.banner.BannerAdRequest
import com.blueseasx.sdk.ad.interstitial.InterstitialAdRequest
import com.blueseasx.sdk.ad.reward.RewardVideoAdRequest
import com.cleveradssolutions.adapters.bluesea.BlueSeaBannerLoader
import com.cleveradssolutions.adapters.bluesea.BlueSeaBidLoader
import com.cleveradssolutions.adapters.bluesea.BuildConfig
import com.cleveradssolutions.adapters.bluesea.getCASErrorForBSCode
import com.cleveradssolutions.adapters.bluesea.toCASError
import com.cleveradssolutions.internal.AdFormatId
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS

class BlueSeaAdapter : MediationRtbAdapter(), BlueseasxSDK.OnInitListener {
    private var mediationInfo = MediationInfo(
        "CAS",
        CAS.getSDKVersion(),
        BuildConfig.VERSION
    )

    override fun getSDKVersion(): String = BlueseasxSDK.getVersionName()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = AdActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.cas or
            AdapterSupport.banner or AdapterSupport.mrec or
            AdapterSupport.inter or AdapterSupport.reward

    override val isInitialized: Boolean
        get() = BlueseasxSDK.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appId = request.requireAppID() ?: return

        request.getStrParameter(Constants.KEY_MEDIATION_NAME)?.let {
            mediationInfo = MediationInfo(
                it,
                request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER) ?: CAS.getSDKVersion(),
                getAdapterVersion()
            )
        }

        onUserPrivacyChanged(request.privacy)
        BlueseasxSDK.init(request.context, appId, this)
    }

    override fun onInitialized() {
        initRequest.onSuccess()
    }

    override fun onInitFailed(code: Int, message: String?) {
        initRequest.onFailure(getCASErrorForBSCode(code, message))
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        if (!isInitialized) {
            request.onFailure(AdError.NOT_INITIALIZED)
            return
        }
        val result = BlueseasxSDK.getToken()
        result.error?.let {
            request.onFailure(it.toCASError())
            return
        }

        val token = result.token
        if (token.isNullOrBlank()) {
            request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR))
            return
        }

        request.onSuccess(token)
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? =
        if (request.format.isAdView)
            loadAd(request as MediationBannerAdRequest)
        else when (request.format.value) {
            AdFormatId.INTERSTITIAL -> loadAd(request as MediationInterstitialAdRequest)
            AdFormatId.REWARDED -> loadAd(request as MediationRewardedAdRequest)
            else -> super.fetchAdBid(request)
        }

    @SuppressLint("WrongThread")
    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN) {
            return super.loadAd(request)
        }

        val view = BSXAdView(request.context)
        view.layoutParams = request.createAdLayout()
        val loader = BlueSeaBannerLoader(request, view)
        view.setAdViewListener(loader)
        val builder = if (request.bidResponse != null) {
            BannerAdRequest.Builder(request.unitId, request.bidResponse)
        } else {
            BannerAdRequest.Builder(request.unitId)
        }
        builder.setMediationInfo(mediationInfo)
        view.loadAd(builder.build())
        return loader
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader =
        BlueSeaBidLoader<InterstitialAd>(request).also {
            val builder = if (request.bidResponse != null) {
                InterstitialAdRequest.Builder(request.unitId, request.bidResponse)
            } else {
                InterstitialAdRequest.Builder(request.unitId)
            }
            builder.setMediationInfo(mediationInfo)
            BlueseasxSDK.loadInterstitialAd(builder.build(), it)
        }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader =
        BlueSeaBidLoader<RewardVideoAd>(request).also {
            val builder = if (request.bidResponse != null) {
                RewardVideoAdRequest.Builder(request.unitId, request.bidResponse)
            } else {
                RewardVideoAdRequest.Builder(request.unitId)
            }
            builder.setMediationInfo(mediationInfo)
            BlueseasxSDK.loadRewardVideoAd(builder.build(), it)
        }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        val context = initRequest.context
        privacy.hasConsent(AdSourceId.BLUESEA)?.let {
            BlueseasxSDK.setUserConsent(
                context,
                Consent.GDPR,
                it
            )
        }

        privacy.isOptOutSaleUS()?.let {
            BlueseasxSDK.setUserConsent(
                context,
                Consent.CCPA,
                !it
            )
        }

        privacy.isAppliesCOPPA()?.let {
            BlueseasxSDK.setUserConsent(
                context,
                Consent.COPPA,
                !it
            )
        }
    }
}