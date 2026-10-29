package com.cleveradssolutions.adapters.verve

import android.view.View
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidMRectAdView

internal class VerveBannerAd(
    request: MediationBannerAdRequest
) : MediationAdBase(), MediationBannerAd, HyBidAdView.Listener, MediationAdLoader {

    private val view: HyBidAdView
    private var request: MediationBannerAdRequest? = request

    init {
        view = if (request.adSizeId == AdSizeId.MREC) {
            HyBidMRectAdView(request.context)
        } else {
            HyBidAdView(request.context, getHyBidAdSize(request.adSizeId))
        }
        view.layoutParams = request.createAdLayout()

        val bid = request.bidResponse
        if (bid != null) {
            view.renderAd(bid, this)
        } else {
            val appToken = HyBid.getAppToken()
            view.load(appToken, request.unitId, this)
        }
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view

    override fun destroy() {
        super.destroy()
        CASHandler.main { view.destroy() }
    }

    override fun onAdLoaded() {
        creativeId = view.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onAdLoadFailed(error: Throwable?) {
        request?.onFailure(error.toVerveAdError())
        request = null
    }

    override fun onAdImpression() {
        listener?.onAdImpression(this)
    }

    override fun onAdClick() {
        listener?.onAdClicked(this)
    }

    private fun getHyBidAdSize(adSizeId: Int): AdSize = when (adSizeId) {
        AdSizeId.DEFAULT -> AdSize.SIZE_320x50
        AdSizeId.LARGE -> AdSize.SIZE_728x90
        AdSizeId.MREC -> AdSize.SIZE_300x250
        else -> AdSize.SIZE_300x50
    }
}
