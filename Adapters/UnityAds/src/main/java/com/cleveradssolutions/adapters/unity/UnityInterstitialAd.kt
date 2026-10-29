package com.cleveradssolutions.adapters.unity

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.unity3d.ads.AdExpiredListener
import com.unity3d.ads.InterstitialAd
import com.unity3d.ads.InterstitialShowListener
import com.unity3d.ads.ShowConfiguration
import com.unity3d.ads.ShowFinishState
import com.unity3d.ads.UnityAdsError

internal class UnityInterstitialAd(
    private val view: InterstitialAd
) : MediationAdBase(),
    MediationScreenAd,
    InterstitialShowListener,
    AdExpiredListener<InterstitialAd> {

    init {
        view.onAdExpired = this
    }

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        val config = ShowConfiguration.Builder().build()
        view.show(activity, config, this)
    }

    override fun onStarted(unityAd: InterstitialAd) {
        listener?.onAdImpression(this)
    }

    override fun onClicked(unityAd: InterstitialAd) {
        listener?.onAdClicked(this)
    }

    override fun onCompleted(unityAd: InterstitialAd, state: ShowFinishState) {
        listener?.onAdDismissed(this)
    }

    override fun onFailed(unityAd: InterstitialAd, error: UnityAdsError) {
        listener?.onAdFailedToShow(this, mapUnityError(error))
    }

    override fun onAdExpired(ad: InterstitialAd) {
        listener?.onAdExpired(this)
    }
}