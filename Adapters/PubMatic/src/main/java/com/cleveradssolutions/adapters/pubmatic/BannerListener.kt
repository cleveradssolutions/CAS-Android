package com.cleveradssolutions.adapters.pubmatic

import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.openwrap.banner.POBBannerView

internal class BannerListener(
    private var loader: PubMaticBidLoader?
) : POBBannerView.POBBannerViewListener() {
    private var loadedAd: MediationAd? = null

    override fun onAdReceived(view: POBBannerView) {
        val loader = loader ?: return
        this.loader = null
        val request = loader.request?.forBannerAd() ?: return

        view.creativeSize?.let {
            val width: Int = it.adWidth
            val height: Int = it.adHeight
            val adSize = request.adSize
            if (width > adSize.width || height > adSize.height) {
                loader.request = null
                request.onFailure(
                    AdError(
                        AdErrorCode.CONFIGURATION_ERROR,
                        "Loaded ad size mismatch"
                    )
                )
                return
            }
            val density: Float = view.context.resources.displayMetrics.density
            view.layoutParams.width = (density * width).toInt()
            view.layoutParams.height = (density * height).toInt()
        }

        val ad = PubMaticAd(view)
        loader.fillAdContent(ad)
        loadedAd = ad
        request.onSuccess(ad)
    }

    override fun onAdFailed(view: POBBannerView, error: POBError) {
        loader?.onBidFailed(view, error)
        loader = null
        loadedAd?.let {
            it.listener?.onAdFailedToShow(it, error.toCASError())
        }
    }

    override fun onAdClicked(view: POBBannerView) {
        loadedAd?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdImpression(view: POBBannerView) {
        loadedAd?.let {
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdOpened(p0: POBBannerView) {
        loadedAd?.let {
            it.listener?.onAdShowed(it)
        }
    }

    override fun onAdClosed(p0: POBBannerView) {
        loadedAd?.let {
            it.listener?.onAdShowed(it)
        }
    }
}
