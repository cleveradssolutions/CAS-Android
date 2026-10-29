package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdInteractionListener
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd

internal class AppOpenAd(val view: PAGAppOpenAd) :
    MediationAdBase(), MediationScreenAd,
    PAGAppOpenAdInteractionListener {

    override fun showScreen(listener: MediationAdListener) {
        // PAGAppOpenAdInteractionCallback is deprecated and showFailed callback not used
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