package com.cleveradssolutions.adapters.moloco

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.AdLoad
import com.moloco.sdk.publisher.AdShowListener
import com.moloco.sdk.publisher.FullscreenAd
import com.moloco.sdk.publisher.MolocoAd
import com.moloco.sdk.publisher.MolocoAdError
import com.moloco.sdk.publisher.RewardedInterstitialAdShowListener

internal class MolocoScreenAd<TListener : AdShowListener, TAd : FullscreenAd<TListener>>(
    request: MediationScreenAdRequest,
    val bidResponse: String
) : MediationAdBase(), MediationScreenAd, MediationAdLoader, RewardedInterstitialAdShowListener,
    AdLoad.Listener, (TAd?, MolocoAdError.AdCreateError?) -> Unit {
    private var request: MediationScreenAdRequest? = request
    var screenAd: TAd? = null

    override fun invoke(createdAd: TAd?, error: MolocoAdError.AdCreateError?) {
        if (createdAd == null) {
            this.request?.onFailure(error.toCASError())
            this.request = null
        } else {
            screenAd = createdAd
            createdAd.load(bidResponse, this)
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        val ad = screenAd
        if (ad?.isLoaded == true) {
            @Suppress("UNCHECKED_CAST")
            ad.show(this as TListener)
        } else {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }

    override fun destroy() {
        super.destroy()
        request = null
        screenAd?.destroy()
        screenAd = null
    }

    override fun onAdLoadSuccess(molocoAd: MolocoAd) {
        creativeId = molocoAd.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onAdLoadFailed(molocoAdError: MolocoAdError) {
        request?.onFailure(molocoAdError.toCASError())
        request = null
    }

    override fun onAdShowSuccess(molocoAd: MolocoAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(molocoAdError: MolocoAdError) {
        listener?.onAdFailedToShow(this, molocoAdError.toCASError())
    }

    override fun onAdClicked(molocoAd: MolocoAd) {
        listener?.onAdClicked(this)
    }

    @Deprecated("No other implementation is available.")
    override fun onRewardedVideoStarted(molocoAd: MolocoAd) = Unit

    @Deprecated("No other implementation is available.")
    override fun onRewardedVideoCompleted(molocoAd: MolocoAd) = Unit

    override fun onUserRewarded(molocoAd: MolocoAd) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdHidden(molocoAd: MolocoAd) {
        listener?.onAdDismissed(this)
    }
}