package com.cleveradssolutions.adapters.displayio

import android.view.View
import com.brandio.ads.ads.Ad
import com.brandio.ads.containers.InlineContainer
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.AdEventListener
import com.brandio.ads.placements.InlinePlacementInterface
import com.brandio.ads.placements.Placement
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd

internal class DisplayIOAd(
    private val placement: Placement,
    private val ad: Ad
) : MediationAdBase(), MediationBannerAd, MediationScreenAd, AdEventListener {

    override fun showScreen(listener: MediationAdListener) {
        this.listener = listener
        ad.setEventListener(this)
        ad.showAd(
            listener.contextService.getActivityOrNull() ?: listener.contextService.getContext()
        )
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        ad.setEventListener(this)

        val inlinePlacement = placement as InlinePlacementInterface
        val holder = InlineContainer.getAdView(request.context)
        holder.layoutParams = request.createAdaptiveAdLayout()
        val container = inlinePlacement.getContainer(ad.requestId)
        container.bindTo(holder)
        return holder
    }

    override fun destroy() {
        super.destroy()
        ad.close()
    }

    override fun onShown(ad: Ad) {
        listener?.onAdImpression(this)
    }

    override fun onAdCompleted(ad: Ad) {
        listener?.onUserEarnedReward(this)
    }

    override fun onClicked(ad: Ad) {
        listener?.onAdClicked(this)
    }

    override fun onClosed(ad: Ad) {
        listener?.onAdDismissed(this)
    }

    override fun onFailedToShow(ad: Ad, error: DIOError) {
        listener?.onAdFailedToShow(
            this,
            error.toCASError()
        )
    }
}