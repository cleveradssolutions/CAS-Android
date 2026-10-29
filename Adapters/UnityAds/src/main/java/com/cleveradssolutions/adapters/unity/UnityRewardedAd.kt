package com.cleveradssolutions.adapters.unity

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.unity3d.ads.AdExpiredListener
import com.unity3d.ads.RewardedAd
import com.unity3d.ads.RewardedShowListener
import com.unity3d.ads.ShowConfiguration
import com.unity3d.ads.ShowFinishState
import com.unity3d.ads.UnityAdsError

internal class UnityRewardedAd(
    private val view: RewardedAd
) : MediationAdBase(),
    MediationScreenAd,
    RewardedShowListener,
    AdExpiredListener<RewardedAd> {

    init {
        view.onAdExpired = this
    }

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        val config = ShowConfiguration.Builder().build()
        view.show(activity, config, this)
    }

    override fun onStarted(unityAd: RewardedAd) {
        listener?.onAdImpression(this)
    }

    override fun onClicked(unityAd: RewardedAd) {
        listener?.onAdClicked(this)
    }

    override fun onRewarded(rewardedAd: RewardedAd) {
        listener?.onUserEarnedReward(this)
    }

    override fun onCompleted(unityAd: RewardedAd, state: ShowFinishState) {
        listener?.onAdDismissed(this)
    }

    override fun onFailed(unityAd: RewardedAd, error: UnityAdsError) {
        listener?.onAdFailedToShow(this, mapUnityError(error))
    }

    override fun onAdExpired(ad: RewardedAd) {
        listener?.onAdExpired(this)
    }
}