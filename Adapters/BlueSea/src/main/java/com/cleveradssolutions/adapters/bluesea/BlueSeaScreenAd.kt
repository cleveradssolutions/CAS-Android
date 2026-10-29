package com.cleveradssolutions.adapters.bluesea

import com.blueseasx.sdk.AdError
import com.blueseasx.sdk.InterstitialAd
import com.blueseasx.sdk.RewardAdListener
import com.blueseasx.sdk.RewardVideoAd
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError as CASError

internal class BlueSeaScreenAd(
    private val ad: InterstitialAd
) : MediationAdBase(), MediationScreenAd, RewardAdListener {

    init {
        creativeId = ad.creativeId
    }

    override val isExpired: Boolean
        get() = super.isExpired || ad.isExpired

    override fun showScreen(listener: MediationAdListener) {
        if (ad.isExpired) {
            listener.onAdFailedToShow(this, CASError.NOT_READY)
            return
        }

        val activity = listener.requireUIContext(this) ?: return

        if (ad is RewardVideoAd) {
            ad.setAdListener(this as RewardAdListener)
        } else {
            ad.setAdListener(this)
        }

        ad.show(activity)
    }

    override fun onAdDisplayFailed(error: AdError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdDisplayed() {
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

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }
}