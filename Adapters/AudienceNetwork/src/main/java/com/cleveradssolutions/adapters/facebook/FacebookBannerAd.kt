package com.cleveradssolutions.adapters.facebook

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.facebook.ads.Ad
import com.facebook.ads.AdListener
import com.facebook.ads.AdSize
import com.facebook.ads.AdView

internal class FacebookBannerAd(
    request: MediationBannerAdRequest,
    fbSize: AdSize
) : MediationAdBase(),
    AdListener, MediationBannerAd, MediationAdLoader {
    private val view: AdView
    private var request: MediationBannerAdRequest? = request

    init {
        // AN Banner can have dynamic width for Adaptive sizes,
        // but should have constant height from defaults 50, 90, 250
        val layout = request.createAdaptiveAdLayout()
        layout.height = request.adSize.findClosestSize()!!.heightPixels(request.context)

        view = AdView(request.context, request.unitId, fbSize)
        view.layoutParams = layout
        view.setWatermark(request)
        view.loadAd(
            view.buildLoadAdConfig()
                .withAdListener(this)
                .withBid(request.bidResponse)
                .build()
        )
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view

    override fun destroy() {
        super.destroy()
        CASHandler.main {
            view.destroy()
        }
    }

    override fun onError(ad: Ad, error: FacebookError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdLoaded(ad: Ad) {
        request?.onSuccess(this)
        request = null
    }

    override fun onAdClicked(ad: Ad) {
        listener?.onAdClicked(this)
    }

    override fun onLoggingImpression(ad: Ad) {
        listener?.onAdImpression(this)
    }
}