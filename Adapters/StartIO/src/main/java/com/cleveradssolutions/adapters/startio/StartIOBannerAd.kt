package com.cleveradssolutions.adapters.startio

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.AdFormat
import com.startapp.sdk.ads.banner.BannerCreator
import com.startapp.sdk.ads.banner.BannerFormat
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.ads.banner.BannerRequest

internal class StartIOBannerAd() : MediationAdBase(), MediationBannerAd, MediationAdLoaderUI,
    BannerListener, BannerRequest.Callback {
    private var request: MediationBannerAdRequest? = null
    private var creator: BannerCreator? = null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        val bannerRequest = request.forBannerAd()
        this.request = bannerRequest
        BannerRequest(request.context)
            .setAdFormat(
                if (request.format == AdFormat.MEDIUM_RECTANGLE) BannerFormat.MREC
                else BannerFormat.BANNER
            )
            .setAdSize(bannerRequest.adSize.width, bannerRequest.adSize.height)
            .setAdPreferences(request.createSIOPreferences())
            .load(this, request.bidResponse)
    }

    override fun destroy() {
        super.destroy()
        creator = null
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = creator!!.create(request.context, this)
        view.layoutParams = request.createAdaptiveAdLayout()
        creator = null
        return view
    }

    override fun onFinished(creator: BannerCreator?, error: String?) {
        val request = request ?: return
        this.request = null
        this.creator = creator
        if (creator != null) {
            request.onSuccess(this)
            return
        }
        disableAutoRetries()
        request.onFailure(getCASError(error, false))
    }

    override fun onReceiveAd(ad: View?) {
        // not used. onFinished instead
    }

    override fun onFailedToReceiveAd(ad: View?) {
        // not used. onFinished instead
    }

    override fun onImpression(ad: View?) {
        listener?.onAdImpression(this)
    }

    override fun onClick(ad: View?) {
        listener?.onAdClicked(this)
    }
}