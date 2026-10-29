package com.cleveradssolutions.adapters.moloco

import android.annotation.SuppressLint
import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.moloco.sdk.publisher.AdLoad
import com.moloco.sdk.publisher.Banner
import com.moloco.sdk.publisher.BannerAdShowListener
import com.moloco.sdk.publisher.CreateBannerCallback
import com.moloco.sdk.publisher.MolocoAd
import com.moloco.sdk.publisher.MolocoAdError

internal class MolocoBannerAd(
    request: MediationBannerAdRequest,
    private val bidResponse: String
) : MediationAdBase(), MediationBannerAd, MediationAdLoader,
    AdLoad.Listener, BannerAdShowListener, CreateBannerCallback {

    private var request: MediationBannerAdRequest? = request
    private var banner: Banner? = null

    override fun invoke(
        createdBanner: Banner?,
        error: MolocoAdError.AdCreateError?
    ) {
        val activeRequest = this.request ?: return
        if (createdBanner == null) {
            this.request = null
            activeRequest.onFailure(error.toCASError())
            return
        }
        banner = createdBanner
        createdBanner.layoutParams = activeRequest.createAdaptiveAdLayout()
        createdBanner.load(bidResponse, this)
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = banner!!
        view.adShowListener = this
        return view
    }

    @SuppressLint("WrongThread")
    override fun destroy() {
        super.destroy()
        request = null
        banner?.destroy()
        banner = null
    }

    override fun onAdLoadSuccess(molocoAd: MolocoAd) {
        creativeId = molocoAd.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onAdLoadFailed(molocoAdError: MolocoAdError) {
        request?.onFailure(molocoAdError.toCASError())
        request = null
    }

    override fun onAdShowSuccess(molocoAd: MolocoAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(molocoAdError: MolocoAdError) {
        listener?.onAdFailedToShow(this, molocoAdError.toCASError())
    }

    override fun onAdClicked(molocoAd: MolocoAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdHidden(molocoAd: MolocoAd) {
    }
}
