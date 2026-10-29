package com.cleveradssolutions.adapters.bidease

import android.view.View
import com.bidease.mobile.ResponseData
import com.bidease.mobile.bannerads.BannerLoadFailure
import com.bidease.mobile.bannerads.BannerLoadResult
import com.bidease.mobile.bannerads.BannerLoadSuccess
import com.bidease.mobile.bannerads.BannerView
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationSingleAdBase
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError

internal class BideaseBannerAd(
    private val view: BannerView,
    private var request: MediationBannerAdRequest?
) : MediationSingleAdBase(), MediationBannerAd, (BannerLoadResult) -> Unit, OnBideaseAdContentLoaded {

    init {
        view.onLoaded = this
        view.onFailed = {
            onAdFailed(it)
        }
    }

    // Load Banner Result
    override fun invoke(result: BannerLoadResult) {
        when (result) {
            is BannerLoadSuccess -> onBidLoaded(result.responseData)

            is BannerLoadFailure -> {
                this.request?.onFailure(
                    if (result.error.startsWith("204")) AdError.NO_FILL
                    else AdError(result.error)
                )
                this.request = null
            }
        }
    }

    fun onBidLoaded(data: ResponseData) {
        data.price?.let {
            costPerMille = it
            revenuePrecision = AdRevenuePrecision.PRECISE
        }

        creativeId = data.requestId + "|" + data.bidId
        // still wait onLoaded after creative loaded
    }

    fun onAdFailed(error: String) {
        val casError = error.getCASError()
        this.request?.onFailure(casError)
        this.request = null
        this.listener?.onAdFailedToShow(this, casError)
    }

    /**
     * Callback [OnBideaseAdContentLoaded]
     */
    override fun invoke() {
        this.request?.onSuccess(this)
        this.request = null
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        view.layoutParams =
            request.createAdLayout()

        view.onDisplayed = {
            this.listener?.onAdImpression(this)
        }

        view.onClicked = {
            this.listener?.onAdClicked(this)
        }

        view.onClosed = {
            this.listener?.onAdDismissed(this)
        }

        return view
    }

    override fun destroy() {
        super.destroy()
        request = null
        CASHandler.main {
            view.destroy()
        }
    }
}