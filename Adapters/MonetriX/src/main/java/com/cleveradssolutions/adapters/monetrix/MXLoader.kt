package com.cleveradssolutions.adapters.monetrix

import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork
import com.monetrix.adsdk.api.banner.BannerAd
import com.monetrix.adsdk.api.base.Ad
import com.monetrix.adsdk.api.base.AdLoadCallback
import com.monetrix.adsdk.api.nativead.NativeAd

internal class MXLoader<T : Ad>(
    private var request: MediationAdUnitRequest?
) : MediationAdBase(), MediationAdBid, MediationAdLoader, AdLoadCallback<T> {
    private var ad: T? = null
    override val bidResponse: String
        get() = "Not used"

    override val isExpired: Boolean
        get() = super.isExpired || ad?.hasExpired() == true

    override fun onError(error: MXError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdLoaded(ad: T) {
        val request = request ?: return
        this.request = null
        creativeId = ad.creativeId
        unitId = request.unitId

        if (ad is BannerAd) {
            val adSize = request.forBannerAd().adSize
            val loadedWidth = ad.width
            val loadedHeight = ad.height
            if (loadedWidth > adSize.width || loadedHeight > adSize.height) {
                request.onFailure(
                    AdError(
                        AdErrorCode.CONFIGURATION_ERROR,
                        "Loaded invalid ad size: ${loadedWidth}x$loadedHeight"
                    )
                )
                try {
                    destroy()
                } catch (_: Throwable) {
                }
                return
            }
        }

        if (request is MediationAdBidRequest) {
            val bid = ad.bid
            if (bid == null) {
                request.onFailure(
                    AdError(AdErrorCode.CONFIGURATION_ERROR, "Not bid ad unit")
                )
                return
            }
            costPerMille = bid.biddingPrice
            revenuePrecision = AdRevenuePrecision.PRECISE
            this.ad = ad

            request.onSuccess(this)
            return
        }

        costPerMille = request.floor
        revenuePrecision = AdRevenuePrecision.FLOOR

        this.ad = null

        onAdContentLoaded(ad, request)
    }

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val ad = ad
        if (ad == null) {
            request.onFailure(AdError("Ad or bid not found"))
            return
        }
        this.ad = null
        ad.bid?.notifyWin(secondPrice, AdNetwork.getDisplayName(secondSource))

        onAdContentLoaded(ad, request)
    }

    private fun onAdContentLoaded(ad: Ad, request: MediationAdUnitRequest) {
        if (ad is NativeAd) {
            val casAd = MonetrixNativeAd(ad)
            casAd.unitId = unitId
            casAd.creativeId = creativeId
            casAd.costPerMille = costPerMille
            casAd.revenuePrecision = revenuePrecision
            val nativeRequest = request.forNativeAd()
            ad.videoController?.mute(nativeRequest.isStartVideoMuted)
            nativeRequest.onSuccess(casAd)
            return
        }

        val casAd = MonetrixAd(ad)
        casAd.unitId = unitId
        casAd.creativeId = creativeId
        casAd.costPerMille = costPerMille
        casAd.revenuePrecision = revenuePrecision
        if (request.format.isAdView)
            request.forBannerAd().onSuccess(casAd)
        else
            request.forScreenAd().onSuccess(casAd)
    }

    override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        val ad = ad ?: return
        this.ad = null
        ad.bid?.notifyLoss(winnerPrice, AdNetwork.getDisplayName(winnerSource), reason)
        ad.destroy()
    }

    override fun destroy() {
        super.destroy()
        ad?.destroy()
        ad = null
    }
}