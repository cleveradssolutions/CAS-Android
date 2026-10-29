package com.cleveradssolutions.adapters.inmobi

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.inmobi.ads.InMobiBanner

internal class IMBannerAd(
    val view: InMobiBanner
) : MediationAdBase(), MediationBannerAd {

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        return view
    }
}