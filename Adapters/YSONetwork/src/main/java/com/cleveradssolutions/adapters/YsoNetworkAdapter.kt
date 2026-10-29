package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.ysonetwork.BuildConfig
import com.cleveradssolutions.adapters.ysonetwork.YsoBannerAd
import com.cleveradssolutions.adapters.ysonetwork.YsoInitListener
import com.cleveradssolutions.adapters.ysonetwork.YsoInterstitialAd
import com.cleveradssolutions.adapters.ysonetwork.YsoRewardedAd
import com.cleveradssolutions.internal.mediation.AdapterSupport
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
import com.ysocorp.ysonetwork.YsoNetwork

// Documentation:
// https://mediation-doc.ysonetwork.com/android/installation

class YsoNetworkAdapter : MediationAdapterBase() {
    override fun getSDKVersion(): String = YsoNetwork.getSdkVersion();
    override fun getAdapterVersion(): String = BuildConfig.VERSION

    override fun getActivityClass(): Class<*> =
        com.ysocorp.ysonetwork.webview.YNWebViewActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.max

    override val isInitialized: Boolean
        get() = YsoNetwork.isInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        // Can throw exception on error.
        try {
            YsoNetwork.initialize(request.context)
            // Yso Initialization is async but have no listener.
            // So we want create own listener to check initialization status.
            YsoInitListener(request).run()
        } catch (e: Throwable) {
            request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, e.toString()))
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        request.onSuccess(YsoNetwork.getSignal().orEmpty())
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? =
        if (isBidResponseFound(request)) {
            // YSO banner have two stages of ad loading:
            // 1. bannerLoad
            // 2. bannerShow to get Ad View
            val ad = YsoBannerAd(request)
            YsoNetwork.bannerLoad(ad.auctionId, request.bidResponse, ad)
            ad
        } else null

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? =
        if (isBidResponseFound(request)) {
            val ad = YsoInterstitialAd(request)
            YsoNetwork.interstitialLoad(ad.auctionId, request.bidResponse, ad)
            ad
        } else null

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? =
        if (isBidResponseFound(request)) {
            val ad = YsoRewardedAd(request)
            YsoNetwork.rewardedLoad(ad.auctionId, request.bidResponse, ad)
            ad
        } else null


    private fun isBidResponseFound(request: MediationAdUnitRequest): Boolean {
        if (request.bidResponse == null) {
            request.onFailure(
                AdError(
                    AdErrorCode.INTERNAL_ERROR,
                    "Supported requests with Bid response only"
                )
            )
            return false
        }
        return true
    }
}