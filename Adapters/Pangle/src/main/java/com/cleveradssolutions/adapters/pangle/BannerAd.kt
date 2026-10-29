package com.cleveradssolutions.adapters.pangle

import android.view.View
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAd
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAdInteractionListener
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest

internal class BannerAd(val ad: PAGBannerAd) :
    MediationAdBase(), MediationBannerAd,
    PAGBannerAdInteractionListener {

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        ad.setAdInteractionListener(this)
        val view = ad.bannerView
        view.layoutParams = request.createAdaptiveAdLayout()
        return view
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }

    override fun onAdShowed() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdDismissed() {

    }

}