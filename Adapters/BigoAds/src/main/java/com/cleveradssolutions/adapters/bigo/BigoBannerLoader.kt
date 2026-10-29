package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import sg.bigo.ads.api.AdSize
import sg.bigo.ads.api.BannerAd
import sg.bigo.ads.api.BannerAdLoader
import sg.bigo.ads.api.BannerAdRequest

internal class BigoBannerLoader(request: MediationBannerAdRequest) :
    BaseLoader<BannerAd, BigoBanner>(request) {

    init {
        // Attention. Bigo can load ANY supported Ad size selected for Ad Slot.
        // Banner Size must be set only one for single Ad Slot on Bigo dashboard.
        val builder = BannerAdRequest.Builder()
            .withSlotId(request.unitId)

        if (request.adSizeId == AdSizeId.MREC)
            builder.withAdSizes(AdSize.MEDIUM_RECTANGLE)
        else {
            val backupSize = if (request.adSizeId == AdSizeId.LARGE)
                AdSize.LEADERBOARD
            else
                AdSize.BANNER
            if (request.adSize.isAdaptive || request.adSize.isInline) {
                builder.withAdSizes(
                    AdSize.getAdaptiveAdSize(request.context, request.adSize.width),
                    backupSize
                )
            } else {
                builder.withAdSizes(backupSize)
            }
        }

        if (!request.bidResponse.isNullOrEmpty())
            builder.withBid(request.bidResponse)

        // BigoAdView(request.context) is wrapper for BannerAdLoader and BannerAd in View
        // No sense use View wrapper here

        BannerAdLoader.Builder()
            .withAdLoadListener(this)
            .withExt(request.getMediationExtString())
            .build()
            .loadAd(builder.build())
    }

    override fun createAd(ad: BannerAd) = BigoBanner(ad)

    override fun onContentLoaded(request: MediationAdUnitRequest, ad: BigoBanner) {
        request.forBannerAd().onSuccess(ad)
    }
}