package com.cleveradssolutions.adapters.inmobi

import android.widget.FrameLayout
import androidx.annotation.MainThread
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdContentRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.inmobi.ads.InMobiBanner


internal class IMBannerAdLoader @MainThread constructor(
    request: MediationBannerAdRequest,
    placementId: Long
) : MediationAdBase(), MediationAdBid, MediationAdLoader {
    private val bannerListener = IMBannerAdListener()
    private val view: InMobiBanner
    private val wrapper: FrameLayout

    override val bidResponse: String
        get() = "Not used"

    init {
        bannerListener.ad = this
        bannerListener.request = request

        val context = request.context
        view = InMobiBanner(context, placementId)
        view.setEnableAutoRefresh(false)
        view.setAnimationType(InMobiBanner.AnimationType.ANIMATION_OFF)
        view.setListener(bannerListener)

        /*
         * We wrap the ad View in a FrameLayout to ensure that it's the right
         * size. Without this the ad takes up the maximum width possible,
         * causing artifacts on high density screens (like the Galaxy Nexus) or
         * in landscape view. If the underlying library sets the appropriate
         * size instead of match_parent, this wrapper can be removed.
         */
        wrapper = FrameLayout(context)

        val layoutParams = request.createAdaptiveAdLayout()
        wrapper.layoutParams = layoutParams
        // InMobi expects the standard banner size in the layout request and reads adaptive
        // dimensions from extras (ab-ad-slot / ab-type).
        wrapper.addView(view, request.createAdLayout())

        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            view.setExtras(CASInMobiExtras(request))
            view.load(bidResponse.toByteArray())
        } else if (request is MediationAdBidRequest) {
            view.setExtras(getBannerExtras(request))
            view.preloadManager.preload()
        } else {
            view.setExtras(getBannerExtras(request))
            view.load()
        }
    }

    override fun loadAdContent(
        request: MediationAdContentRequest,
        secondPrice: Double,
        secondSource: Int
    ) {
        val view = view
        bannerListener.request = request.forBannerAd()
        CASHandler.main {
            view.preloadManager.load()
        }
    }

    override fun noticeLoss(reason: Int, winnerPrice: Double, winnerSource: Int) {
        // do nothing
    }
}