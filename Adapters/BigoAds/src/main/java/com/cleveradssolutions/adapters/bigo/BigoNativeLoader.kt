package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import sg.bigo.ads.api.NativeAd
import sg.bigo.ads.api.NativeAdLoader
import sg.bigo.ads.api.NativeAdRequest

internal class BigoNativeLoader(
    request: MediationNativeAdRequest
) : BaseLoader<NativeAd, BigoNativeContent>(request) {

    init {
        val bigoRequest = NativeAdRequest.Builder()
            .withSlotId(request.unitId)

        if (!request.bidResponse.isNullOrEmpty())
            bigoRequest.withBid(request.bidResponse)

        NativeAdLoader.Builder()
            .withAdLoadListener(this)
            .withExt(request.getMediationExtString())
            .build()
            .loadAd(bigoRequest.build())
    }

    override fun createAd(ad: NativeAd): BigoNativeContent {
        val content = BigoNativeContent(ad)
        ad.setAdInteractionListener(content)
        return content
    }

    override fun onContentLoaded(request: MediationAdUnitRequest, ad: BigoNativeContent) {
        request.forNativeAd().onSuccess(ad)
    }
}