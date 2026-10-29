package com.cleveradssolutions.adapters.pangle

import android.util.Log
import com.bytedance.sdk.openadsdk.api.PAGClientBidding
import com.bytedance.sdk.openadsdk.api.PAGLoadListener
import com.bytedance.sdk.openadsdk.api.PangleAd
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAd
import com.cleveradssolutions.mediation.bidding.AuctionNoticeReason
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork
import java.util.Locale

internal abstract class BaseBidLoader<PangleAdType, CASAdType>(
    private var request: MediationAdBidRequest?
) : MediationAdBase(),
    PAGLoadListener<PangleAdType>, MediationAdBid
        where PangleAdType : PangleAd,
              PangleAdType : PAGClientBidding,
              CASAdType : MediationAd {
    private var loadedAd: PangleAdType? = null
    final override val bidResponse: String
        get() = "Not used"

    final override val isExpired: Boolean
        get() = super.isExpired || loadedAd == null

    final override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }

    final override fun onAdLoaded(ad: PangleAdType?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }

        request.debugExtraInfo(ad)

        // Request with BidResponse not want use Price from ExtraInfo
        // Pangle send not valid price in this case
        if (request.bidResponse != null) {
            costPerMille = request.floor
            revenuePrecision = AdRevenuePrecision.PRECISE
            onContentLoaded(request, ad)
            return
        }

        // The unit of price is ecpm(effective Cost Per Mille, in dollar)
        // Can contains any Number, not Double only.
        // Also cast Int as? Double = null, so we want do Number.toDouble() instead
        val price = (ad.getExtraInfo("price") as? Number)?.toDouble()
        if (price != null) {
            costPerMille = price
            revenuePrecision = AdRevenuePrecision.PRECISE
        } else {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "Not bidding placement"))
            return
        }

        loadedAd = ad
        creativeId = ad.getExtraInfo("tag_id")?.toString()

        request.onSuccess(this)
    }

    private fun onContentLoaded(
        request: MediationAdUnitRequest,
        ad: PangleAdType
    ) = when (ad) {
        is PAGBannerAd -> {
            val content = BannerAd(ad)
            content.creativeId = creativeId
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.forBannerAd().onSuccess(content)
        }

        is PAGInterstitialAd -> {
            val content = InterstitialAd(ad)
            content.creativeId = creativeId
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.forScreenAd().onSuccess(content)
        }

        is PAGRewardedAd -> {
            val content = RewardedAd(ad)
            content.creativeId = creativeId
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.forScreenAd().onSuccess(content)
        }

        is PAGAppOpenAd -> {
            val content = AppOpenAd(ad)
            content.creativeId = creativeId
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.forScreenAd().onSuccess(content)
        }

        is PAGNativeAd -> {
            val content = NativeAdContent(ad)
            content.creativeId = creativeId
            content.revenuePrecision = revenuePrecision
            content.costPerMille = costPerMille
            request.forNativeAd().onSuccess(content)
        }

        else -> {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Loaded not supported ad type ${ad::class.java}"
                )
            )
        }
    }

    final override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val client = loadedAd
        if (client == null) {
            request.onFailure(
                AdError(AdErrorCode.INTERNAL_ERROR, "Bid client is null")
            )
            return
        }
        client.win(secondPrice)
        onContentLoaded(request, client)
    }

    final override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        val client = loadedAd
        if (client == null) {
            Log.e("CAS.AI", "Bigo Bid client is null")
            return
        }
        val winnerCode = when (winnerSource) {
            AdSourceId.AUDIENCE_NETWORK -> "fan"
            AdSourceId.GOOGLE_ADS -> "admob"
            AdSourceId.APPLOVIN -> "applovin"
            AdSourceId.IRONSOURCE -> "is"
            else -> AdNetwork.getDisplayName(winnerSource).lowercase(Locale.ENGLISH)
        }

        // Other codes not declarated
        val reasonCode = when (reason) {
            AuctionNoticeReason.IMPRESSION_EXPIRED -> "2"
            AuctionNoticeReason.NOT_HIGHER_THAN_WATERFALL,
            AuctionNoticeReason.NOT_HIGHEST_BIDDER -> "102"

            AuctionNoticeReason.BELOW_AUCTION_FLOOR -> "100"
            else -> "1" // Internal Error
        }

        // The unit of price is ecpm(effective Cost Per Mille, in dollar)
        client.loss(winnerPrice, reasonCode, winnerCode)
    }

    override fun destroy() {
        super.destroy()
        loadedAd = null
    }
}