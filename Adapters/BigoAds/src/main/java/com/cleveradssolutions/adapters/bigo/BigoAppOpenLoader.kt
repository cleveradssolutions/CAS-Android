package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import sg.bigo.ads.api.SplashAd
import sg.bigo.ads.api.SplashAdLoader
import sg.bigo.ads.api.SplashAdRequest

internal class BigoAppOpenLoader(request: MediationScreenAdRequest) :
    BaseLoader<SplashAd, BigoAppOpenAd>(request) {

    init {
        val builder = SplashAdRequest.Builder()
            .withSlotId(request.unitId)
            .withAppLogo(request.context.applicationInfo.logo)
            .withAppName(request.targeting.appName)

        if (!request.bidResponse.isNullOrEmpty())
            builder.withBid(request.bidResponse)

        SplashAdLoader.Builder()
            .withAdLoadListener(this)
            .withExt(request.getMediationExtString())
            .build()
            .loadAd(builder.build())
    }

    override fun createAd(ad: SplashAd): BigoAppOpenAd = BigoAppOpenAd(ad)
    override fun onContentLoaded(request: MediationAdUnitRequest, ad: BigoAppOpenAd) {
        request.forScreenAd().onSuccess(ad)
    }

}