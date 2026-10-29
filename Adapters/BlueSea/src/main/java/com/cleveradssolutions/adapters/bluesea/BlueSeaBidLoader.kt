package com.cleveradssolutions.adapters.bluesea

import com.blueseasx.sdk.Ad
import com.blueseasx.sdk.AdLoadCallback
import com.blueseasx.sdk.BSXAdView
import com.blueseasx.sdk.InterstitialAd
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork

internal open class BlueSeaBidLoader<T: Ad>(
    request: MediationAdUnitRequest
) : MediationAdBase(), MediationAdBid, MediationAdLoader, AdLoadCallback<T> {

    var request: MediationAdUnitRequest? = request
    private var bidAd: T? = null

    override val bidResponse: String
        get() = "Not used"

    override fun onAdLoadError(error: com.blueseasx.sdk.AdError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdLoaded(ad: T) {
        val request = request
        this.request = null

        if (request == null) {
            ad.destroy()
            return
        }

        if (request is MediationAdBidRequest) {
            val price = ad.price
            if (price > 0.0) {
                unitId = request.unitId
                costPerMille = price
                revenuePrecision = AdRevenuePrecision.PRECISE
                bidAd = ad
                request.onSuccess(this)
                return
            }

            ad.destroy()
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "BlueSea placement did not return a bidding price"
                )
            )
            return
        }

        onContentLoaded(request, ad)
    }

    private fun onContentLoaded(request: MediationAdUnitRequest, ad: Ad) {
        if (ad is BSXAdView) {
            val bannerAd = BlueSeaBannerAd(ad).also {
                it.unitId = request.unitId
                it.costPerMille = costPerMille
                it.revenuePrecision = revenuePrecision
            }
            request.forBannerAd().onSuccess(bannerAd)
            return
        }
        val screenAd = BlueSeaScreenAd(ad as InterstitialAd).also {
            it.unitId = request.unitId
            it.costPerMille = costPerMille
            it.revenuePrecision = revenuePrecision
        }
        request.forScreenAd().onSuccess(screenAd)
    }

    override val isExpired: Boolean
        get() = super.isExpired || bidAd?.isExpired == true

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val ad = bidAd
        if (ad == null) {
            request.onFailure(
                AdError(
                    AdErrorCode.INTERNAL_ERROR,
                    "Bid ad is null"
                )
            )
            return
        }
        bidAd = null
        ad.notifyAdWin(
            AdNetwork.getDisplayName(secondSource),
            secondPrice
        )
        onContentLoaded(request, ad)
    }

    override fun noticeLoss(
        reason: Int,
        winnerPrice: Double,
        winnerSource: Int
    ) {
        bidAd?.let {
            it.notifyAdLoss(
                AdNetwork.getDisplayName(winnerSource),
                winnerPrice,
                reason
            )
            it.destroy()
        }
        bidAd = null
    }

    override fun destroy() {
        super.destroy()
        request = null
        bidAd?.let {
            bidAd = null
            CASHandler.main {
                it.destroy()
            }
        }
    }
}