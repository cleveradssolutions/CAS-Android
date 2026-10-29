package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import sg.bigo.ads.api.RewardVideoAd
import sg.bigo.ads.api.RewardVideoAdLoader
import sg.bigo.ads.api.RewardVideoAdRequest

internal class BigoRewardedLoader(request: MediationScreenAdRequest) :
    BaseLoader<RewardVideoAd, BigoRewarded>(request) {

    init {
        val builder = RewardVideoAdRequest.Builder()
            .withSlotId(request.unitId)

        if (!request.bidResponse.isNullOrEmpty())
            builder.withBid(request.bidResponse)

        RewardVideoAdLoader.Builder()
            .withAdLoadListener(this)
            .withExt(request.getMediationExtString())
            .build()
            .loadAd(builder.build())
    }

    override fun createAd(ad: RewardVideoAd) = BigoRewarded(ad)

    override fun onContentLoaded(request: MediationAdUnitRequest, ad: BigoRewarded) {
        request.forScreenAd().onSuccess(ad)
    }
}