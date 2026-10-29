package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.RewardAdInteractionListener
import sg.bigo.ads.api.RewardVideoAd


internal class BigoRewarded(val view: RewardVideoAd) : MediationAdBase(),
    RewardAdInteractionListener, MediationScreenAd {

    override val isExpired: Boolean
        get() = super.isExpired || view.isExpired

    override fun showScreen(listener: MediationAdListener) {
        if (view.isExpired) {
            listener.onAdFailedToShow(this, com.cleversolutions.ads.AdError.NOT_READY)
        } else {
            view.setAdInteractionListener(this)
            view.show(listener.contextService.getActivityOrNull())
        }
    }

    override fun destroy() {
        super.destroy()
        view.destroy()
    }

    override fun onAdError(error: AdError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdImpression() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdOpened() {
        listener?.onAdShowed(this)
    }

    override fun onAdClosed() {
        listener?.onAdDismissed(this)
    }

    override fun onAdRewarded() {
        listener?.onUserEarnedReward(this)
    }
}