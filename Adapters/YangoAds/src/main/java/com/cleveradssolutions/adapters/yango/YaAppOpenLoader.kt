package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader
import com.yandex.mobile.ads.common.AdRequestError

internal class YaAppOpenLoader : MediationAdLoaderUI,
    AppOpenAdLoadListener {
    private var request: MediationScreenAdRequest? = null
    private var loader: AppOpenAdLoader? = null

    fun isLoading(): Boolean = request != null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        if (!request.requirePortraitOrientationOnly()) {
            return
        }

        this.request = request.forScreenAd()
        val loader = loader
            ?: AppOpenAdLoader(request.context).also {
                this.loader = it
            }

        loader.loadAd(request.toYaRequest(), this)
    }

    override fun onAdLoaded(appOpenAd: AppOpenAd) {
        request?.let {
            request = null
            it.onSuccess(YaAppOpenAd(appOpenAd))
        }
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        request?.onYaFailure(error)
        request = null
    }
}