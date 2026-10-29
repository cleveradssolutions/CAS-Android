package com.cleveradssolutions.adapters.maticoo

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.mediation.core.MediationSingleAdBase
import com.cleversolutions.ads.AdError
import com.maticoo.sdk.ad.video.RewardedVideoAd
import com.maticoo.sdk.ad.video.RewardedVideoListener
import com.zmaticoo.sdk.ads.rewardads.MaticooRewardInfo
import com.zmaticoo.sdk.base.common.MaticooIds
import com.zmaticoo.sdk.flow.model.ComponentError

internal class MaticooRewardedAd(
    request: MediationScreenAdRequest,
    extras: Map<String, Any>
) : MediationSingleAdBase(), MediationScreenAd, MediationAdLoader {
    private var ids = MaticooIds(false, request.unitId, request.bidResponse)

    private val maticooListener = object : RewardedVideoListener(), MediationAdLoader {
        private var request: MediationScreenAdRequest? = request

        override fun onRewardedVideoAdLoadSuccess(ids: MaticooIds) {
            this@MaticooRewardedAd.ids = ids
            this.request?.onSuccess(this@MaticooRewardedAd)
            this.request = null
        }

        override fun onRewardedVideoAdLoadFailed(p0: MaticooIds?, error: ComponentError) {
            this.request?.onFailure(error.toCASError())
            this.request = null
        }

        override fun onRewardedVideoAdShowed(p0: MaticooIds?) {
            listener?.onAdImpression(this@MaticooRewardedAd)
        }

        override fun onRewardedVideoAdShowFailed(p0: MaticooIds?, error: ComponentError) {
            listener?.onAdFailedToShow(this@MaticooRewardedAd, error.toCASError())
        }

        override fun onRewardedVideoAdRewarded(p0: MaticooIds?, p1: MaticooRewardInfo?) {
            listener?.onUserEarnedReward(this@MaticooRewardedAd)
        }

        override fun onRewardedVideoAdClicked(p0: MaticooIds?) {
            listener?.onAdClicked(this@MaticooRewardedAd)
        }

        override fun onRewardedVideoAdClosed(ids: MaticooIds) {
            listener?.onAdDismissed(this@MaticooRewardedAd)
        }
    }

    init {
        RewardedVideoAd.setAdListener(request.unitId, maticooListener)
        val bidResponse = request.bidResponse
        if (bidResponse != null)
            RewardedVideoAd.loadAd(request.unitId, bidResponse, extras)
        else
            RewardedVideoAd.loadAd(request.unitId, extras)
    }

    override fun showScreen(listener: MediationAdListener) {
        if (RewardedVideoAd.isReady(ids)) {
            RewardedVideoAd.setAdListener(unitId, maticooListener)
            RewardedVideoAd.showAd(ids)
            return
        }
        listener.onAdFailedToShow(this, AdError.NOT_READY)
    }

    override fun destroy() {
        RewardedVideoAd.destroy(unitId)
        super.destroy()
    }
}