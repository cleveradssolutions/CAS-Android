package com.cleveradssolutions.adapters.pubmatic

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.pubmatic.sdk.openwrap.banner.POBBannerView
import com.pubmatic.sdk.openwrap.core.POBBidEvent
import com.pubmatic.sdk.openwrap.interstitial.POBInterstitial
import com.pubmatic.sdk.rewardedad.POBRewardedAd

internal class PubMaticAd(
    private val view: POBBidEvent
) : MediationAdBase(), MediationScreenAd, MediationBannerAd {

    override fun showScreen(listener: MediationAdListener) {
        when (view) {
            is POBInterstitial -> view.show()
            is POBRewardedAd -> view.show()
            else -> listener.onAdFailedToShow(
                this,
                AdError("Not implemented ${view::class.java.simpleName}")
            )
        }
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view as View

    override fun destroy() {
        super.destroy()
        CASHandler.main {
            when (view) {
                is POBInterstitial -> view.destroy()
                is POBRewardedAd -> view.destroy()
                is POBBannerView -> view.destroy()
            }
        }
    }
}