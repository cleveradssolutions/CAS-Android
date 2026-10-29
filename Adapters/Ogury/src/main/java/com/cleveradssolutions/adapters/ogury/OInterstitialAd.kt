package com.cleveradssolutions.adapters.ogury

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import com.ogury.ad.OguryAdError
import com.ogury.ad.OguryInterstitialAd
import com.ogury.ad.OguryInterstitialAdListener
import com.ogury.ad.common.OguryMediation

internal class OInterstitialAd(request: MediationInterstitialAdRequest, mediation: OguryMediation) :
    MediationAdBase(),
    OguryInterstitialAdListener, MediationScreenAd, MediationAdLoader {
    private var view =
        OguryInterstitialAd(request.context, request.unitId, mediation)
    private var request: MediationInterstitialAdRequest? = request

    init {
        view.setListener(this)
        if (view.isLoaded()) {
            request.onSuccess(this)
        } else {
            view.load(request.bidResponse)
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        if (view.isLoaded()) {
            view.show()
            listener.onAdShowed(this)
        } else {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }

    override fun onAdLoaded(ad: OguryInterstitialAd) {
        request?.onSuccess(this)
        request = null
    }

    override fun onAdError(ad: OguryInterstitialAd, error: OguryAdError) {
        if (error.type == OguryAdError.Type.SHOW_ERROR) {
            listener?.onAdFailedToShow(this, error.toCASError())
        } else {
            request?.onFailure(error.toCASError())
            request = null
        }
    }

    override fun onAdClicked(ad: OguryInterstitialAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdClosed(ad: OguryInterstitialAd) {
        listener?.onAdDismissed(this)
    }

    override fun onAdImpression(ad: OguryInterstitialAd) {
        listener?.onAdImpression(this)
    }
}