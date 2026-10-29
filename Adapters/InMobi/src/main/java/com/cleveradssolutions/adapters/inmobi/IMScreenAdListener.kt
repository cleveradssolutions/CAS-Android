package com.cleveradssolutions.adapters.inmobi

import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus
import com.inmobi.ads.InMobiInterstitial
import com.inmobi.ads.listeners.InterstitialAdEventListener

internal class IMScreenAdListener : InterstitialAdEventListener() {
    var request: MediationScreenAdRequest? = null
    var ad: MediationAd? = null

    override fun onAdFetchSuccessful(view: InMobiInterstitial, info: AdMetaInfo) {
        if (info.tryHandleFetchInMobiAd(ad, request)) {
            request = null
            ad = null
        }
    }

    override fun onAdFetchFailed(view: InMobiInterstitial, status: InMobiAdRequestStatus) {
        request?.onFailure(status.toCASError())
        request = null
        ad = null
    }

    override fun onAdLoadFailed(view: InMobiInterstitial, status: InMobiAdRequestStatus) {
        onAdFetchFailed(view, status)
    }

    override fun onAdLoadSucceeded(view: InMobiInterstitial, info: AdMetaInfo) {
        val request = request ?: return
        this.request = null
        val ad = IMScreenAd(view)
        info.fillAdInfo(ad, request)
        this.ad = ad

        request.onSuccess(ad)
    }

    override fun onAdDisplayed(view: InMobiInterstitial, info: AdMetaInfo) {
        ad?.let {
            it.creativeId = info.creativeID
            it.listener?.onAdShowed(it)
        }
    }

    override fun onAdImpression(view: InMobiInterstitial) {
        ad?.let {
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdDisplayFailed(view: InMobiInterstitial) {
        ad?.let {
            it.listener?.onAdFailedToShow(it, AdError(AdErrorCode.INTERNAL_ERROR))
        }
    }

    override fun onAdClicked(view: InMobiInterstitial, p1: MutableMap<Any, Any>) {
        ad?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onRewardsUnlocked(view: InMobiInterstitial, p1: MutableMap<Any, Any>) {
        ad?.let {
            it.listener?.onUserEarnedReward(it)
        }
    }

    override fun onAdDismissed(view: InMobiInterstitial) {
        ad?.let {
            it.listener?.onAdDismissed(it)
        }
    }
}