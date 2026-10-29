package com.cleveradssolutions.adapters.hyprmx

import android.view.View
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleversolutions.ads.AdError
import com.hyprmx.android.sdk.banner.HyprMXBannerListener
import com.hyprmx.android.sdk.banner.HyprMXBannerSize.HyprMXAdSizeBanner
import com.hyprmx.android.sdk.banner.HyprMXBannerSize.HyprMXAdSizeLeaderboard
import com.hyprmx.android.sdk.banner.HyprMXBannerSize.HyprMXAdSizeMediumRectangle
import com.hyprmx.android.sdk.banner.HyprMXBannerView
import com.hyprmx.android.sdk.placement.HyprMXLoadAdListener

internal class HMXBannerAd() : MediationAdBase(),
    MediationBannerAd, MediationAdLoaderUI,
    HyprMXBannerListener, HyprMXLoadAdListener {
    private var view: HyprMXBannerView? = null
    private var request: MediationBannerAdRequest? = null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        val bannerRequest = request.forBannerAd()
        this.request = bannerRequest

        val adSize = when (bannerRequest.adSizeId) {
            AdSizeId.LARGE -> HyprMXAdSizeLeaderboard
            AdSizeId.MREC -> HyprMXAdSizeMediumRectangle
            else -> HyprMXAdSizeBanner
        }
        view = HyprMXBannerView(
            request.context,
            null,
            request.unitId,
            adSize
        ).also {
            it.listener = this
            it.layoutParams = bannerRequest.createAdLayout()
            val bidResponse = request.bidResponse
            if (bidResponse != null)
                it.loadAd(bidResponse, this)
            else
                it.loadAd(this)
        }
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view!!

    override fun destroy() {
        super.destroy()
        view?.let {
            view = null
            it.listener = null
            it.destroy()
        }
    }

    override fun onAdLoaded(isAdAvailable: Boolean) {
        val request = request ?: return
        this.request = null
        if (isAdAvailable) request.onSuccess(this)
        else request.onFailure(AdError.NO_FILL)
    }

    override fun onAdOpened(view: HyprMXBannerView) {

    }

    override fun onAdClicked(view: HyprMXBannerView) {
        listener?.onAdClicked(this)
    }

    override fun onAdClosed(view: HyprMXBannerView) {

    }

    override fun onAdImpression(view: HyprMXBannerView) {
        listener?.onAdImpression(this)
    }

    override fun onAdLeftApplication(view: HyprMXBannerView) {
    }
}