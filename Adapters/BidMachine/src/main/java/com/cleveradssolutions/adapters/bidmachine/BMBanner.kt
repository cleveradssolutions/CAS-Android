package com.cleveradssolutions.adapters.bidmachine

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import io.bidmachine.banner.BannerListener
import io.bidmachine.banner.BannerRequest
import io.bidmachine.banner.BannerView
import io.bidmachine.utils.BMError
import com.cleversolutions.ads.AdError as CASError

internal class BMBannerAd(
    request: MediationBannerAdRequest
) : MediationAdBase(), MediationBannerAd, MediationAdLoader, BannerListener {

    private var request: MediationBannerAdRequest? = request
    private val view = BannerView(request.context).also {
        it.layoutParams = request.createAdaptiveAdLayout()
        it.setListener(this)
    }

    fun load(request: MediationBannerAdRequest) {
        load(request.buildBMBannerRequest().build())
    }

    fun load(adRequest: BannerRequest) {
        view.load(adRequest)
    }

    override val isExpired: Boolean
        get() = super.isExpired || view.isExpired

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view

    override fun onAdLoaded(ad: BannerView) {
        val request = this.request ?: return
        this.request = null
        ad.auctionResult?.let { creativeId = it.creativeId }
        request.onSuccess(this)
    }

    override fun onAdLoadFailed(ad: BannerView, error: BMError) {
        val request = this.request
        this.request = null
        request?.onFailure(error.toCASError())
    }

    override fun onAdExpired(ad: BannerView) {
        val request = this.request
        this.request = null
        request?.onFailure(CASError.EXPIRED)
        setAdExpired()
    }

    override fun onAdImpression(ad: BannerView) {
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(ad: BannerView, error: BMError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdClicked(ad: BannerView) {
        listener?.onAdClicked(this)
    }

    override fun destroy() {
        super.destroy()
        request = null
        CASHandler.main {
            view.destroy()
        }
    }
}
