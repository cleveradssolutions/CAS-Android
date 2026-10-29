package com.cleveradssolutions.adapters.bidease

import android.annotation.SuppressLint
import com.bidease.mobile.BidResult
import com.bidease.mobile.bannerads.BannerView
import com.bidease.mobile.interstitialads.InterstitialAdBase
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork

/**
 * Callback for [InterstitialAd.bid]
 */
internal typealias OnBideaseAdBidResult = (BidResult) -> Unit

/**
 * Callback for [InterstitialAd.onLoaded]
 */
internal typealias OnBideaseAdContentLoaded = () -> Unit

internal fun String.getCASError(): AdError {
    return if (startsWith("204"))
        AdError.NO_FILL
    else if (this == "not loaded") AdError.NOT_READY
    else AdError(this)
}

internal class BideaseBidLoader(
    request: MediationAdBidRequest,
    private var bannerAd: BannerView?,
    private var screenAd: InterstitialAdBase?
) : MediationAdBase(), MediationAdBid, MediationAdLoader, OnBideaseAdBidResult {

    private var request: MediationAdBidRequest? = request
    private var bid: BidResult.Success? = null
    private var keepAdContent: MediationAd? = null

    override val bidResponse: String
        get() = "Not used"

    override val isExpired: Boolean
        get() = super.isExpired || bid == null

    /**
     * Callback [OnBideaseAdBidResult]
     */
    override fun invoke(result: BidResult) {
        when (result) {
            is BidResult.Success -> onBidSuccess(result)
            is BidResult.Failure -> onBidFailure(result.error)
        }
    }

    private fun onBidSuccess(result: BidResult.Success) {
        val request = request ?: return
        this.request = null

        val price = result.responseData.price
        if (price == null || price < 0.00001) {
            result.notifyLoss(
                "unknown",
                0.0
            )
            request.onFailure(
                AdError(AdErrorCode.CONFIGURATION_ERROR, "Loaded bid ad with no price")
            )
            return
        }

        bid = result
        costPerMille = price
        revenuePrecision = AdRevenuePrecision.PRECISE

        result.onExpired = {
            setAdExpired()
            destroy()
        }

        request.onSuccess(this)
    }

    private fun onBidFailure(error: String) {
        request?.onFailure(error.getCASError())
        request = null
    }

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val bid = bid
        if (bid == null) {
            request.onFailure(AdError.NOT_READY)
            return
        }

        this.bid = null

        val bannerAd = bannerAd
        if (bannerAd != null) {
            val content = BideaseBannerAd(bannerAd, request.forBannerAd())
            content.onBidLoaded(bid.responseData)
            keepAdContent = content
            bid.notifyWin()
            @SuppressLint("WrongThread")
            bannerAd.load(bid)
            this.bannerAd = null
            return
        }

        val screenAd = screenAd
        if (screenAd != null) {
            val content = BideaseScreenAd(screenAd, request.forScreenAd())
            content.onBidLoaded(bid.responseData)
            keepAdContent = content
            bid.notifyWin()
            screenAd.load(bid)
            this.screenAd = null
            return
        }

        request.onFailure(AdError("Bid ad object not found"))
    }

    override fun noticeLoss(
        reason: Int,
        winnerPrice: Double,
        winnerSource: Int
    ) {
        bid?.notifyLoss(
            AdNetwork.getDisplayName(winnerSource),
            winnerPrice
        )
    }

    override fun destroy() {
        super.destroy()

        request = null
        bid = null
        if (bannerAd == null && screenAd == null)
            return
        CASHandler.main {
            bannerAd?.destroy()
            bannerAd = null

            screenAd?.destroy()
            screenAd = null
        }
    }
}