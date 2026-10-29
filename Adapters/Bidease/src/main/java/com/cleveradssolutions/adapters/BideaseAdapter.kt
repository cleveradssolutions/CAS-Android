package com.cleveradssolutions.adapters

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import com.bidease.ads.adapter.applovin.BideaseMediationAdapter
import com.bidease.mobile.BideaseMobile
import com.bidease.mobile.ForegroundActivityTracker
import com.bidease.mobile.InitFailure
import com.bidease.mobile.InitParams
import com.bidease.mobile.InitSuccess
import com.bidease.mobile.LoadParams
import com.bidease.mobile.PrivacyParams
import com.bidease.mobile.ads.AdSize
import com.bidease.mobile.bannerads.BannerView
import com.bidease.mobile.interstitialads.InterstitialAd
import com.bidease.mobile.interstitialads.InterstitialAdActivity
import com.bidease.mobile.rewardedads.RewardedAd
import com.cleveradssolutions.adapters.bidease.BideaseBannerAd
import com.cleveradssolutions.adapters.bidease.BideaseBidLoader
import com.cleveradssolutions.adapters.bidease.BideaseScreenAd
import com.cleveradssolutions.adapters.bidease.BuildConfig
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS

class BideaseAdapter : MediationRtbAdapter() {
    override fun getSDKVersion(): String = BideaseMobile.VERSION
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = InterstitialAdActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.max

    override val isInitialized: Boolean
        get() = BideaseMobile.isInitialized

    override fun initAds(request: MediationInitAdRequest) {
        if (request.requireAppID() == null) return

        if (request.privacy.isAppliesCOPPA() == true) {
            request.onFailure(
                AdError(
                    AdErrorCode.REJECTED,
                    "Bidease does not support child-directed traffic"
                )
            )
            return
        }

        request.contextService.getActivityOrNull()?.let {
            ForegroundActivityTracker.onActivityResumed(it)
        }

        BideaseMobile.initialize(
            request.context,
            createInitParams(request)
        ) { result ->
            when (result) {
                is InitSuccess -> {
                    request.onSuccess()
                }

                is InitFailure -> {
                    request.onFailure(AdError(result.error))
                }
            }
        }
    }

    // Configured to main thread call in AdSourceConfig
    @MainThread
    override fun loadAd(
        request: MediationBannerAdRequest
    ): MediationAdLoader? {
        val size = when (request.forBannerAd().adSizeId) {
            AdSizeId.MREC -> AdSize.BANNER_300x250
            AdSizeId.LARGE -> AdSize.BANNER_728x90
            AdSizeId.DEFAULT -> AdSize.BANNER_320x50
            else -> return super.loadAd(request)
        }
        val view = BannerView(request.context)

        val loader = BideaseBannerAd(view, request)
        view.load(size, createLoadParams(request), loader)
        return loader
    }

    override fun loadAd(
        request: MediationInterstitialAdRequest
    ): MediationAdLoader {
        val ad = InterstitialAd(request.context)
        val loader = BideaseScreenAd(ad, request)
        ad.load(createLoadParams(request), loader)
        return loader
    }

    override fun loadAd(
        request: MediationRewardedAdRequest
    ): MediationAdLoader {
        val ad = RewardedAd(request.context)
        val loader = BideaseScreenAd(ad, request)
        ad.load(createLoadParams(request), loader)
        return loader
    }

    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        @SuppressLint("WrongThread")
        if (request.format.isAdView) {
            val size = when (request.forBannerAd().adSizeId) {
                AdSizeId.MREC -> AdSize.BANNER_300x250
                AdSizeId.LARGE -> AdSize.BANNER_728x90
                AdSizeId.DEFAULT -> AdSize.BANNER_320x50
                else -> return super.fetchAdBid(request)
            }
            val banner = BannerView(request.context)
            val loader = BideaseBidLoader(request, banner, null)
            banner.bid(size, createLoadParams(request), loader)
            return loader
        }
        when (request.format.value) {
            AdFormat.INTERSTITIAL.value -> {
                val ad = InterstitialAd(request.context)
                val loader = BideaseBidLoader(request, null, ad)
                ad.bid(createLoadParams(request), loader)
                return loader
            }

            AdFormat.REWARDED.value -> {
                val ad = RewardedAd(request.context)
                val loader = BideaseBidLoader(request, null, ad)
                ad.bid(createLoadParams(request), loader)
                return loader
            }

            else -> {
                return super.fetchAdBid(request)
            }
        }
    }

    override fun migrateToMediation(sourceId: Int) {
        //super.migrateToMediation(sourceId)

        BideaseMediationAdapter.setInitParams(
            createInitParams(initRequest)
        )
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        BideaseMobile.setPrivacyParams(
            createPrivacyParams(privacy)
        )
    }

    private fun createInitParams(
        request: MediationInitAdRequest
    ) = InitParams(
        key = request.appID,
        privacyParams = createPrivacyParams(request.privacy)
    )

    private fun createPrivacyParams(
        privacy: MediationUserPrivacy
    ) = PrivacyParams(
        subjectToGdpr = privacy.isAppliesGDPR(),
        userConsentString = privacy.getConsentString(),
        usPrivacyString = privacy.getUSPrivacyString()
    )

    internal fun createLoadParams(
        request: MediationAdUnitRequest
    ) = LoadParams(
        bidFloor = request.floor.takeIf { it > 0.0 },
        placementName = request.unitId,
        testMode = if (request.isTestRequest) true else null,
        userId = request.targeting.userId,
        age = CAS.targetingOptions.age.takeIf { it > 0 }
    )
}