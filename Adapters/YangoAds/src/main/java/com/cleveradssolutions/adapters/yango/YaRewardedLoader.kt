package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader

internal class YaRewardedLoader : MediationAdLoaderUI,
    RewardedAdLoadListener {
    private var request: MediationScreenAdRequest? = null
    private var loader: RewardedAdLoader? = null

    fun isLoading(): Boolean = request != null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        this.request = request.forScreenAd()
        val loader = loader
            ?: RewardedAdLoader(request.context).also {
                this.loader = it
            }

        loader.loadAd(request.toYaRequest(), this)
    }

    override fun onAdLoaded(rewarded: RewardedAd) {
        request?.let {
            request = null
            it.onSuccess(YaRewardedAd(rewarded))
        }
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        request?.onYaFailure(error)
        request = null
    }
}