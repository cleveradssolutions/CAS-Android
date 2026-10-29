package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import sg.bigo.ads.api.InterstitialAd
import sg.bigo.ads.api.InterstitialAdLoader
import sg.bigo.ads.api.InterstitialAdRequest

internal class BigoInterstitialLoader(request: MediationScreenAdRequest) :
    BaseLoader<InterstitialAd, BigoInterstitial>(request) {
    init {
        val builder = InterstitialAdRequest.Builder()
            .withSlotId(request.unitId)

        if (!request.bidResponse.isNullOrEmpty())
            builder.withBid(request.bidResponse)

        InterstitialAdLoader.Builder()
            .withAdLoadListener(this)
            .withExt(request.getMediationExtString())
            .build()
            .loadAd(builder.build())
    }

    override fun createAd(ad: InterstitialAd) = BigoInterstitial(ad)

    override fun onContentLoaded(request: MediationAdUnitRequest, ad: BigoInterstitial) {
        request.forScreenAd().onSuccess(ad)
    }
}