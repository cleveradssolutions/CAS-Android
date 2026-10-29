package com.cleveradssolutions.adapters.bidmachine

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import io.bidmachine.interstitial.InterstitialAd
import io.bidmachine.interstitial.InterstitialListener
import io.bidmachine.interstitial.InterstitialRequest
import io.bidmachine.utils.BMError

/**
 * Owns the BidMachine ad for every flow: it is loaded either with the request that
 * won a client auction, or with a fresh one built for a Waterfall or Server Bidding
 * request.
 */
internal class BMInterstitialAd(
    request: MediationScreenAdRequest
) : MediationAdBase(), MediationScreenAd, MediationAdLoader, InterstitialListener {

    private var request: MediationScreenAdRequest? = request
    private val ad = InterstitialAd(request.context).also { it.setListener(this) }

    fun load(request: MediationInterstitialAdRequest){
        ad.load(request.buildBMInterstitialRequest().build())
    }

    fun load(bidRequest: InterstitialRequest) {
        ad.load(bidRequest)
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

    override fun onAdLoaded(ad: InterstitialAd) {
        val request = this.request ?: return
        this.request = null
        creativeId = ad.auctionResult?.creativeId
        request.onSuccess(this)
    }

    override fun onAdLoadFailed(ad: InterstitialAd, error: BMError) {
        val request = this.request
        this.request = null
        request?.onFailure(error.toCASError())
    }

    override fun onAdExpired(ad: InterstitialAd) {
        val request = this.request
        this.request = null
        request?.onFailure(AdError.EXPIRED)
        setAdExpired()
    }

    override fun onAdImpression(ad: InterstitialAd) {
        listener?.onAdShowed(this)
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(ad: InterstitialAd, error: BMError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdClicked(ad: InterstitialAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdClosed(ad: InterstitialAd, finished: Boolean) {
        listener?.onAdDismissed(this)
    }

    override fun destroy() {
        super.destroy()
        request = null
        ad.destroy()
    }
}