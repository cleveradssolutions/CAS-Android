package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

internal class YaInterstitialLoader : MediationAdLoaderUI,
    InterstitialAdLoadListener {
    private var request: MediationScreenAdRequest? = null
    private var loader: InterstitialAdLoader? = null

    fun isLoading(): Boolean = request != null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        this.request = request.forScreenAd()
        val loader = loader
            ?: InterstitialAdLoader(request.context).also {
                this.loader = it
            }

        loader.loadAd(request.toYaRequest(), this)
    }

    override fun onAdLoaded(interstitialAd: InterstitialAd) {
        request?.let {
            request = null
            it.onSuccess(YaInterstitialAd(interstitialAd))
        }
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        request?.onYaFailure(error)
        request = null
    }
}