package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdLoadListener
import com.yandex.mobile.ads.nativeads.NativeAdLoader

internal class YaNativeAdLoader : MediationAdLoaderUI,
    NativeAdLoadListener {
    private var request: MediationNativeAdRequest? = null
    private var loader: NativeAdLoader? = null

    fun isLoading(): Boolean = request != null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        this.request = request.forNativeAd()

        val loader = loader
            ?: NativeAdLoader(request.context).also {
                this.loader = it
            }

        loader.loadAd(request.toYaRequest(), this)
    }

    override fun onAdLoaded(nativeAd: NativeAd) {
        val request = request ?: return
        this.request = null
        request.onSuccess(YaNativeAdContent(nativeAd))
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        request?.onYaFailure(error)
        request = null
    }

}