package com.cleveradssolutions.adapters.verve

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd

internal class VerveInterstitialAd(
    request: MediationInterstitialAdRequest
) : MediationAdBase(), MediationScreenAd, HyBidInterstitialAd.Listener, MediationAdLoader {

    private var ad: HyBidInterstitialAd? = null
    private var request: MediationInterstitialAdRequest? = request

    init {
        val bid = request.bidResponse
        if (bid != null) {
            ad = HyBidInterstitialAd(request.context, "", this)
            ad?.prepareAd(bid)
        } else {
            val appToken = HyBid.getAppToken()
            ad = HyBidInterstitialAd(request.context, appToken, request.unitId, this)
            ad?.load()
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        ad?.let {
            if (it.isReady) {
                it.show()
                return
            }
        }
        listener.onAdFailedToShow(this, AdError.NOT_READY)
    }

    override fun destroy() {
        super.destroy()
        ad?.destroy()
        ad = null
    }

    override fun onInterstitialLoaded() {
        creativeId = ad?.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        request?.onFailure(error.toVerveAdError())
        request = null
    }

    override fun onInterstitialShowFailed(error: Throwable?) {
        listener?.onAdFailedToShow(this, error.toVerveAdError())
    }

    override fun onInterstitialImpression() {
        listener?.onAdImpression(this)
    }

    override fun onInterstitialClick() {
        listener?.onAdClicked(this)
    }

    override fun onInterstitialDismissed() {
        listener?.onAdDismissed(this)
    }
}
