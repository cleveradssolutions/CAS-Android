package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.reward.PAGRewardItem
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAd
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAdInteractionListener
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd

internal class RewardedAd(val view: PAGRewardedAd) :
    MediationAdBase(), MediationScreenAd,
    PAGRewardedAdInteractionListener {

    override fun showScreen(listener: MediationAdListener) {
        view.setAdInteractionListener(this)
        view.show(listener.contextService.getActivityOrNull())
    }

    override fun onAdShowed() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdDismissed() {
        listener?.onAdDismissed(this)
    }

    override fun onUserEarnedReward(p0: PAGRewardItem?) {
        listener?.onUserEarnedReward(this)
    }

    override fun onUserEarnedRewardFail(p0: Int, p1: String?) {

    }
}