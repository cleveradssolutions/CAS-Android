package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionListener
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd

internal class InterstitialAd(val view: PAGInterstitialAd) :
    MediationAdBase(), MediationScreenAd,
    PAGInterstitialAdInteractionListener {

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
}