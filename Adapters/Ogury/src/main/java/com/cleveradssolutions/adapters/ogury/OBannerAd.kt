package com.cleveradssolutions.adapters.ogury

import android.view.View
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.ogury.ad.OguryAdError
import com.ogury.ad.OguryBannerAdSize
import com.ogury.ad.OguryBannerAdView
import com.ogury.ad.OguryBannerAdViewListener
import com.ogury.ad.common.OguryMediation

internal class OBannerAd(
    request: MediationBannerAdRequest,
    mediation: OguryMediation
) : MediationAdBase(), MediationBannerAd, MediationAdLoader,
    OguryBannerAdViewListener {

    private var view: OguryBannerAdView
    private var request: MediationBannerAdRequest? = request

    init {
        val adSize = when (request.adSizeId) {
            AdSizeId.MREC -> OguryBannerAdSize.MREC_300x250
            else -> OguryBannerAdSize.SMALL_BANNER_320x50
        }
        view = OguryBannerAdView(
            request.context,
            request.unitId,
            adSize,
            mediation
        )
        view.setListener(this)
        view.layoutParams = request.createAdLayout()
        view.load(request.bidResponse)
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        return view
    }

    override fun destroy() {
        super.destroy()
        view.destroy()
    }

    override fun onAdLoaded(ad: OguryBannerAdView) {
        request?.onSuccess(this)
        request = null
    }

    override fun onAdError(ad: OguryBannerAdView, error: OguryAdError) {
        if (error.type == OguryAdError.Type.SHOW_ERROR) {
            listener?.onAdFailedToShow(this, error.toCASError())
        } else {
            request?.onFailure(error.toCASError())
            request = null
        }
    }

    override fun onAdClicked(ad: OguryBannerAdView) {
        listener?.onAdClicked(this)
    }

    override fun onAdImpression(ad: OguryBannerAdView) {
        listener?.onAdImpression(this)
    }

    override fun onAdClosed(ad: OguryBannerAdView) {

    }
}