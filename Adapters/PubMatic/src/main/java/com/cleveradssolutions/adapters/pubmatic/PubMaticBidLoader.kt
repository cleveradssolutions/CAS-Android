package com.cleveradssolutions.adapters.pubmatic

import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.nativead.POBNativeAd
import com.pubmatic.sdk.nativead.POBNativeAdLoader
import com.pubmatic.sdk.nativead.POBNativeAdLoaderListener
import com.pubmatic.sdk.openwrap.core.POBBid
import com.pubmatic.sdk.openwrap.core.POBBidEvent
import com.pubmatic.sdk.openwrap.core.POBBidEventListener

internal class PubMaticBidLoader(
    request: MediationAdUnitRequest,
    var ad: POBBidEvent?
) : MediationAdBase(), MediationAdBid, MediationAdLoader,
    POBBidEventListener, POBNativeAdLoaderListener {
    var request: MediationAdUnitRequest? = request

    init {
        costPerMille = request.floor
        // Support bidding only
        revenuePrecision = AdRevenuePrecision.PRECISE
    }

    // MARK: POBBidEventListener

    override fun onBidReceived(ad: POBBidEvent, bid: POBBid) {
        val request = request ?: return
        this.request = null

        this.ad = ad
        creativeId = bid.creativeId
        costPerMille = bid.price

        if (request is MediationAdBidRequest) {
            request.onSuccess(this)
            return
        }
        request.onFailure(AdError("Invalid request. Required MediationAdBidRequest only"))
    }

    override fun onBidFailed(ad: POBBidEvent, error: POBError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    // MARK: MediationAdBid

    override val bidResponse: String
        get() = "Not used"

    override val isExpired: Boolean
        get() = super.isExpired || ad?.bid?.isExpired == true

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val ad = ad
        if (ad == null) {
            request.onFailure(AdError("Bid not found for load content"))
            return
        }
        this.ad = null
        this.request = request
        CASHandler.main {
            ad.proceedToLoadAd()
        }
    }

    fun fillAdContent(ad: MediationAd) {
        ad.creativeId = creativeId
        ad.costPerMille = costPerMille
        ad.revenuePrecision = AdRevenuePrecision.PRECISE
    }

    override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        val ad = ad ?: return
        this.ad = null
        CASHandler.main {
            ad.proceedOnError(POBBidEvent.BidEventError.CLIENT_SIDE_AUCTION_LOSS, "loss")
        }
    }

    // MARK: POBNativeAdLoaderListener

    override fun onAdReceived(loader: POBNativeAdLoader, ad: POBNativeAd) {
        val request = request ?: return
        this.request = null
        val content = PubMaticNativeAd(ad)
        fillAdContent(content)
        request.forNativeAd().onSuccess(content)
    }

    override fun onFailedToLoad(ad: POBNativeAdLoader, error: POBError) {
        request?.onFailure(error.toCASError())
        request = null
    }
}