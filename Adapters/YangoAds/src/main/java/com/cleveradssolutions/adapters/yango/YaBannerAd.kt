package com.cleveradssolutions.adapters.yango

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

internal class YaBannerAd : MediationAdBase(),
    MediationBannerAd, MediationAdLoaderUI,
    BannerAdEventListener {

    private var request: MediationBannerAdRequest? = null
    private var view: BannerAdView? = null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        val bannerRequest = request.forBannerAd()

        val context = request.context
        val view = BannerAdView(context)

        view.visibility = View.VISIBLE
        if (view.visibility != View.VISIBLE) {
            request.onFailure(AdError(AdErrorCode.REJECTED, "Ad blocked by OS"))
            return
        }

        this.request = bannerRequest
        this.view = view

        view.setAdSize(bannerRequest.getYaBannerSize())
        view.setBannerAdEventListener(this)
        //view.setBackgroundColor(Color.TRANSPARENT)
        view.layoutParams = bannerRequest.createAdaptiveAdLayout()

        view.loadAd(request.toYaRequest())
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = view!!
        // Yandex can set LayoutParams.WRAP_CONTENT for height
        view.layoutParams = request.createAdaptiveAdLayout()
        return view
    }

    override fun destroy() {
        super.destroy()
        view?.let {
            view = null
            it.setBannerAdEventListener(null)
            it.destroy()
        }
    }

    override fun onAdLoaded() {
        creativeId = view?.adInfo?.findCreativeId()
        request?.onSuccess(this)
        request = null
    }

    override fun onAdFailedToLoad(error: AdRequestError) {
        request?.onYaFailure(error)
        request = null
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onImpression(impressionData: ImpressionData?) {
        onYaImpression(impressionData)
    }
}