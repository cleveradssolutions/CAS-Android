package com.cleveradssolutions.adapters.bigo

import android.util.Log
import com.cleveradssolutions.mediation.bidding.AuctionNoticeReason
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdNetwork
import sg.bigo.ads.api.Ad
import sg.bigo.ads.api.AdBid
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdLoadListener
import com.cleversolutions.ads.AdError as CASError

internal abstract class BaseLoader<BigoAdType : Ad, CASAdType : MediationAd>(
    request: MediationAdUnitRequest
) : MediationAdBase(),
    AdLoadListener<BigoAdType>,
    MediationAdBid, MediationAdLoader {
    private var request: MediationAdUnitRequest? = request
    private var bid: AdBid? = null
    private var ad: CASAdType? = null
    final override var bidResponse: String? = null

    abstract fun createAd(ad: BigoAdType): CASAdType
    abstract fun onContentLoaded(request: MediationAdUnitRequest, ad: CASAdType)

    final override val isExpired: Boolean
        get() = super.isExpired || ad.let { it == null || it.isExpired }

    final override fun onError(error: AdError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    final override fun onAdLoaded(ad: BigoAdType) {
        val request = request
        this.request = null
        if (request == null) {
            runCatching {
                ad.destroy()
            }
            return
        }
        if (request is MediationAdBidRequest) {
            val bidPrice = ad.bid?.price
            if (bidPrice == null) {
                try {
                    ad.destroy()
                } catch (e: Throwable) {
                    request.debug("Destroy ad failed", e)
                }
                request.onFailure(
                    CASError(
                        AdErrorCode.CONFIGURATION_ERROR,
                        "Not bidding placement"
                    )
                )
                return
            }
            bidResponse = "Not used"
            this.bid = ad.bid
            revenuePrecision = AdRevenuePrecision.PRECISE
            costPerMille = bidPrice
        }

        val content = createAd(ad)
        creativeId = ad.creativeId
        content.creativeId = creativeId
        if (request is MediationAdBidRequest) {
            this.ad = content
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.onSuccess(this)
        } else {
            onContentLoaded(request, content)
        }
    }

    private fun convertSource(sourceId: Int) = when (sourceId) {
        AdSourceId.APPLOVIN -> "Applovin"
        AdSourceId.IRONSOURCE -> "ironSource"
        AdSourceId.LIFTOFF_MONETIZE -> "Liftoff"
        AdSourceId.CUSTOM -> null
        else -> AdNetwork.getDisplayName(sourceId)
    }

    final override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val client = bid
        if (client == null) {
            request.onFailure(
                CASError(AdErrorCode.INTERNAL_ERROR, "Bid client is null")
            )
            return
        }
        val mediationAd = ad
        if (mediationAd == null) {
            request.onFailure(
                CASError(AdErrorCode.INTERNAL_ERROR, "Bid Ad is null")
            )
            return
        }
        client.notifyWin(secondPrice, convertSource(secondSource))
        onContentLoaded(request, mediationAd)
    }

    final override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        val client = bid
        if (client == null) {
            Log.e("CAS.AI", "Bigo Bid client is null")
            return
        }

        // Other codes not decelerated
        val reasonCode = when (reason) {
            AuctionNoticeReason.BELOW_DEAL_FLOOR,
            AuctionNoticeReason.NOT_HIGHER_THAN_WATERFALL,
            AuctionNoticeReason.NOT_HIGHEST_BIDDER ->
                AdBid.LOSS_REASON_LOWER_THAN_HIGHEST_PRICE

            AuctionNoticeReason.BELOW_AUCTION_FLOOR ->
                AdBid.LOSS_REASON_LOWER_THAN_FLOOR_PRICE

            else -> AdBid.LOSS_REASON_INTERNAL_ERROR
        }

        // The unit of price is ecpm(effective Cost Per Mille, in dollar)
        client.notifyLoss(winnerPrice, convertSource(winnerSource), reasonCode)
    }

    override fun destroy() {
        super.destroy()
        bid = null
        ad = null
    }
}