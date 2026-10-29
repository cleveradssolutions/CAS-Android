package com.cleveradssolutions.adapters.bidease

import com.bidease.mobile.ResponseData
import com.bidease.mobile.interstitialads.InterstitialAdBase
import com.bidease.mobile.interstitialads.InterstitialLoadFailure
import com.bidease.mobile.interstitialads.InterstitialLoadResult
import com.bidease.mobile.interstitialads.InterstitialLoadSuccess
import com.bidease.mobile.rewardedads.RewardedAd
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.mediation.core.MediationSingleAdBase
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.base.CASHandler

internal class BideaseScreenAd(
    private val ad: InterstitialAdBase,
    private var request: MediationScreenAdRequest?
) : MediationSingleAdBase(), MediationScreenAd, (InterstitialLoadResult) -> Unit,
    OnBideaseAdContentLoaded {

    init {
        ad.onLoaded = this
        ad.onFailed = {
            onAdFailed(it)
        }
    }

    // Load Interstitial result
    override fun invoke(result: InterstitialLoadResult) {
        when (result) {
            is InterstitialLoadSuccess -> onBidLoaded(result.responseData)
            is InterstitialLoadFailure -> onAdFailed(result.error)
        }
    }

    fun onBidLoaded(data: ResponseData) {
        data.price?.let {
            costPerMille = it
            revenuePrecision = AdRevenuePrecision.PRECISE
        }

        creativeId = data.requestId + "|" + data.bidId
        // still wait onLoaded after creative loaded
    }

    /**
     * Callback [OnBideaseAdContentLoaded]
     */
    override fun invoke() {
        this.request?.onSuccess(this)
        this.request = null
    }

    fun onAdFailed(error: String) {
        val casError = error.getCASError()
        this.request?.onFailure(casError)
        this.request = null
        this.listener?.onAdFailedToShow(this, casError)
    }

    override fun showScreen(
        listener: MediationAdListener
    ) {
        ad.onDisplayed = {
            this.listener?.onAdImpression(this)
        }

        ad.onClicked = {
            this.listener?.onAdClicked(this)
        }

        ad.onClosed = {
            this.listener?.onAdDismissed(this)
        }

        if (ad is RewardedAd) {
            ad.onRewarded = {
                this.listener?.onUserEarnedReward(this)
            }
        }

        ad.show()
    }

    override fun destroy() {
        super.destroy()
        request = null
        CASHandler.main {
            ad.destroy()
        }
    }
}