package com.cleveradssolutions.adapters.maticoo

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.mediation.core.MediationSingleAdBase
import com.cleversolutions.ads.AdError
import com.maticoo.sdk.ad.interstitial.InterstitialAd
import com.maticoo.sdk.ad.interstitial.InterstitialAdListener
import com.zmaticoo.sdk.base.common.MaticooIds
import com.zmaticoo.sdk.flow.model.ComponentError

internal class MaticooInterstitialAd(request: MediationScreenAdRequest, extras:Map<String, Any>) :
    MediationSingleAdBase(), MediationScreenAd, MediationAdLoader {

    private var ids = MaticooIds(false, request.unitId, request.bidResponse)

    private val maticooListener = object : InterstitialAdListener() {
        private var request: MediationScreenAdRequest? = request

        override fun onAdLoadSuccess(ids: MaticooIds) {
            this@MaticooInterstitialAd.ids = ids
            this.request?.onSuccess(this@MaticooInterstitialAd)
            this.request = null
        }

        override fun onAdLoadFailed(ids: MaticooIds, error: ComponentError) {
            this.request?.onFailure(error.toCASError())
            this.request = null
        }

        override fun onAdDisplayed(ids: MaticooIds) {
            listener?.onAdImpression(this@MaticooInterstitialAd)
        }

        override fun onAdDisplayFailed(ids: MaticooIds, error: ComponentError) {
            listener?.onAdFailedToShow(this@MaticooInterstitialAd, error.toCASError())
        }

        override fun onAdClicked(ids: MaticooIds) {
            listener?.onAdClicked(this@MaticooInterstitialAd)
        }

        override fun onAdClosed(ids: MaticooIds) {
            listener?.onAdDismissed(this@MaticooInterstitialAd)
        }
    }

    init {
        InterstitialAd.setAdListener(request.unitId, maticooListener)
        val bidResponse = request.bidResponse
        if (bidResponse != null)
            InterstitialAd.loadAd(request.unitId, bidResponse, extras)
        else
            InterstitialAd.loadAd(request.unitId, extras)
    }

    override fun showScreen(listener: MediationAdListener) {
        if (InterstitialAd.isReady(ids)) {
            InterstitialAd.setAdListener(unitId, maticooListener)
            InterstitialAd.showAd(ids)
            return
        }
        listener.onAdFailedToShow(this, AdError.NOT_READY)
    }

    override fun destroy() {
        InterstitialAd.destroy(unitId)
        super.destroy()
    }
}