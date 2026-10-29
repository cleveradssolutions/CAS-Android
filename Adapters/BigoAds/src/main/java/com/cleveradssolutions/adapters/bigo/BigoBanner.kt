package com.cleveradssolutions.adapters.bigo

import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdInteractionListener
import sg.bigo.ads.api.AdSize
import sg.bigo.ads.api.BannerAd


internal class BigoBanner(val view: BannerAd) : MediationAdBase(),
    AdInteractionListener, MediationBannerAd {

    override val isExpired: Boolean
        get() = super.isExpired || view.isExpired

    override fun createView(request: MediationBannerAdRequest, listener: MediationAdListener): View {
        // Can return null if Expired or have another error
        // if setAdInteractionListener() is set then
        // all errors will be fired to onAdError() callback
        val adView = view.adView()!!
        // Bigo Ad can be loaded in not correct size
        // if on dashboard selected All sizes

        // adView.layoutParams is always null
        // (adView as FrameLayout).getChildAt(0).layoutParams is MATCH PARENT

        // We can set max size of View, but WebView is still be larger then parent

        val layoutParams: ViewGroup.LayoutParams = request.createAdaptiveAdLayout()
        layoutParams.width = AdSize.dp2px(request.context, view.width)
        layoutParams.height = AdSize.dp2px(request.context, view.height)
        adView.setLayoutParams(layoutParams)

        view.setAdInteractionListener(this)
        return adView
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
        // Not used
    }

    override fun onAdClosed() {
        // Not used
    }
}