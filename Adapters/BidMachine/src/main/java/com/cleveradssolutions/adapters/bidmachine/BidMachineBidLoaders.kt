package com.cleveradssolutions.adapters.bidmachine

import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdNetwork
import io.bidmachine.AdRequest
import io.bidmachine.banner.BannerRequest
import io.bidmachine.interstitial.InterstitialRequest
import io.bidmachine.models.AuctionResult
import io.bidmachine.nativead.NativeRequest
import io.bidmachine.rewarded.RewardedRequest
import io.bidmachine.utils.BMError

/**
 * Client Bidding only: BidMachine runs the auction on the device, reports a price,
 * and the creative is loaded later, once the bid has won.
 *
 * The Waterfall and Server Bidding flows never reach here, they load a creative at once.
 */
@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
internal abstract class BidMachineBidLoader<AdRequestType : AdRequest<AdRequestType, *, *>>() :
    MediationAdBase(),
    MediationAdBid, MediationAdLoader,
    AdRequest.AdRequestListener<AdRequestType> {
    protected var bidRequest: AdRequestType? = null
    protected var request: MediationAdBidRequest? = null
    private var contentAd: MediationAdBase? = null

    abstract fun fetchBid(request: MediationAdBidRequest)

    /**
     * Loads the creative of the won auction and reports the ready ad to [contentRequest].
     * Returns that ad, which is held here until it is handed over to CAS: between
     * the load and its callback the only other reference to it lives inside the
     * BidMachine SDK, and we cannot rely on that one staying strong.
     */
    abstract fun loadAdContent(
        contentRequest: MediationAdContentRequest,
        bidRequest: AdRequestType
    ): MediationAdBase

    override fun onRequestSuccess(request: AdRequestType, result: AuctionResult) {
        val request = this.request ?: return
        this.request = null

        costPerMille = result.price
        revenuePrecision = AdRevenuePrecision.PRECISE

        request.onSuccess(this)
    }

    override fun onRequestFailed(request: AdRequestType, error: BMError) {
        // Support comment: a failed request has to be destroyed and built anew.
        request.destroy()
        this.request?.onFailure(error.toCASError())
        this.request = null
    }

    override fun onRequestExpired(request: AdRequestType) {
        request.destroy()
        this.request?.onFailure(AdError.EXPIRED)
        this.request = null
        setAdExpired()
        bidRequest = null
    }

    // MARK: MediationAdBid

    // The creative never leaves the BidMachine request object,
    // so there is no markup for CAS to carry.
    override val bidResponse: String
        get() = "Not used"

    override val isExpired: Boolean
        get() = super.isExpired || bidRequest?.isExpired == true

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val bidRequest = bidRequest
        if (bidRequest == null) {
            request.onFailure(AdError.EXPIRED)
            return
        }
        bidRequest.notifyMediationWin()
        contentAd = loadAdContent(request, bidRequest)
    }

    override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        bidRequest?.notifyMediationLoss(AdNetwork.getDisplayName(winnerSource), winnerPrice)
    }

    override fun destroy() {
        super.destroy()
        request = null

        // Destroying the BidMachine request destroys the ad loaded from it:
        // AdRequest.destroy() reaches the ad through onRequestDestroyed, and a
        // later show() then fails with BMError.Destroyed. So the request is only
        // destroyed while no ad has taken it over; once one has, that ad owns it
        // and destroys it in its own destroy().
        if (contentAd == null) {
            bidRequest?.destroy()
        }
        // The ad is destroyed by CAS once it has been handed over, and goes away
        // with this loader while it has not.
        contentAd = null
        bidRequest = null
    }
}

internal class BidMachineInterstitialBidLoader() : BidMachineBidLoader<InterstitialRequest>() {
    override fun fetchBid(request: MediationAdBidRequest) {
        this.request = request
        val bidRequest = request.forScreenAd()
            .buildBidMachineInterstitialRequest()
            .setListener(this)
            .build()
        this.bidRequest = bidRequest
        bidRequest.request(request.context)
    }

    override fun loadAdContent(
        contentRequest: MediationAdContentRequest,
        bidRequest: InterstitialRequest
    ) = BidMachineInterstitialAd(contentRequest.forScreenAd()).also {
        it.costPerMille = costPerMille
        it.revenuePrecision = revenuePrecision
        it.load(bidRequest)
    }
}

internal class BidMachineRewardedBidLoader() : BidMachineBidLoader<RewardedRequest>() {
    override fun fetchBid(request: MediationAdBidRequest) {
        this.request = request
        val bidRequest = request.forScreenAd()
            .buildBidMachineRewardedRequest()
            .setListener(this)
            .build()
        this.bidRequest = bidRequest
        bidRequest.request(request.context)
    }

    override fun loadAdContent(
        contentRequest: MediationAdContentRequest,
        bidRequest: RewardedRequest
    ) = BidMachineRewardedAd(contentRequest.forScreenAd()).also {
        it.costPerMille = costPerMille
        it.revenuePrecision = revenuePrecision
        it.load(bidRequest)
    }
}


internal class BidMachineBannerBidLoader() : BidMachineBidLoader<BannerRequest>() {
    override fun fetchBid(request: MediationAdBidRequest) {
        this.request = request
        val bidRequest = request.forBannerAd()
            .buildBidMachineBannerRequest()
            .setListener(this)
            .build()
        this.bidRequest = bidRequest
        bidRequest.request(request.context)
    }

    override fun loadAdContent(
        contentRequest: MediationAdContentRequest,
        bidRequest: BannerRequest
    ) = BidMachineBannerAd(contentRequest.forBannerAd()).also {
        it.costPerMille = costPerMille
        it.revenuePrecision = revenuePrecision
        it.load(bidRequest)
    }
}

internal class BidMachineNativeBidLoader() : BidMachineBidLoader<NativeRequest>() {
    override fun fetchBid(request: MediationAdBidRequest) {
        this.request = request
        val bidRequest = request.forNativeAd()
            .buildBidMachineNativeRequest()
            .setListener(this)
            .build()
        this.bidRequest = bidRequest
        bidRequest.request(request.context)
    }

    override fun loadAdContent(
        contentRequest: MediationAdContentRequest,
        bidRequest: NativeRequest
    ) = BidMachineNativeAd(contentRequest.forNativeAd()).also {
        it.costPerMille = costPerMille
        it.revenuePrecision = revenuePrecision
        it.load(bidRequest)
    }
}