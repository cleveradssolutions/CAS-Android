package com.cleveradssolutions.adapters.inmobi

import androidx.annotation.MainThread
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.inmobi.ads.InMobiInterstitial

internal class IMScreenAdLoader @MainThread constructor(
    request: MediationScreenAdRequest,
    placementId: Long
) : MediationAdBase(), MediationAdBid, MediationAdLoader {
    private val screenListener = IMScreenAdListener()
    private var view: InMobiInterstitial

    override val bidResponse: String
        get() = "Not used"

    init {
        screenListener.request = request
        screenListener.ad = this

        view = InMobiInterstitial(request.context, placementId, screenListener)
        view.setExtras(CASInMobiExtras(request))

        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            view.load(bidResponse.toByteArray())
        } else if (request is MediationAdBidRequest) {
            view.preloadManager.preload()
        } else {
            view.load()
        }
    }

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val view = view
        screenListener.request = request.forScreenAd()
        CASHandler.main {
            view.preloadManager.load()
        }
    }

    override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        // do nothing
    }
}