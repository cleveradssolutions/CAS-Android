package com.cleveradssolutions.adapters.bidmachine

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import io.bidmachine.rewarded.RewardedAd
import io.bidmachine.rewarded.RewardedListener
import io.bidmachine.rewarded.RewardedRequest
import io.bidmachine.utils.BMError

/**
 * Owns the BidMachine ad for every flow: it is loaded either with the request that
 * won a client auction, or with a fresh one built for a Waterfall or Server Bidding
 * request.
 */
internal class BMRewardedAd(
    request: MediationScreenAdRequest
) : MediationAdBase(), MediationScreenAd, MediationAdLoader, RewardedListener {

    private var request: MediationScreenAdRequest? = request
    private val ad = RewardedAd(request.context).also { it.setListener(this) }

    fun load(request: MediationRewardedAdRequest){
        ad.load(request.buildBMRewardedRequest().build())
    }

    fun load(adRequest: RewardedRequest) {
        ad.load(adRequest)
    }

    override val isExpired: Boolean
        get() = super.isExpired || ad.isExpired

    override fun showScreen(listener: MediationAdListener) {
        if (!ad.canShow()) {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
            return
        }
        ad.show()
    }

    override fun onAdLoaded(ad: RewardedAd) {
        val request = this.request ?: return
        this.request = null
        creativeId = ad.auctionResult?.creativeId
        request.onSuccess(this)
    }

    override fun onAdLoadFailed(ad: RewardedAd, error: BMError) {
        val request = this.request
        this.request = null
        request?.onFailure(error.toCASError())
    }

    override fun onAdExpired(ad: RewardedAd) {
        val request = this.request
        this.request = null
        request?.onFailure(AdError.EXPIRED)
        setAdExpired()
    }

    override fun onAdImpression(ad: RewardedAd) {
        listener?.onAdShowed(this)
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(ad: RewardedAd, error: BMError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdClicked(ad: RewardedAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdRewarded(ad: RewardedAd) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdClosed(ad: RewardedAd, finished: Boolean) {
        listener?.onAdDismissed(this)
    }

    override fun destroy() {
        super.destroy()
        request = null
        ad.destroy()
    }
}