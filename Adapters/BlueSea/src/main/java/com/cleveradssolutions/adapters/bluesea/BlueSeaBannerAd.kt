package com.cleveradssolutions.adapters.bluesea

import android.view.View
import com.blueseasx.sdk.AdError
import com.blueseasx.sdk.BSXAdView
import com.blueseasx.sdk.BSXAdViewListener
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler

internal class BlueSeaBannerAd(
    private val adView: BSXAdView
) : MediationAdBase(), MediationBannerAd, BSXAdViewListener {

    init {
        creativeId = adView.creativeId
        adView.setAdViewListener(this)
    }

    override val isExpired: Boolean
        get() = super.isExpired || adView.isExpired

    override fun createView(request: MediationBannerAdRequest, listener: MediationAdListener): View {
        adView.layoutParams = request.createAdLayout()
        return adView
    }

    override fun onAdLoaded() {
        // implementation in BannerLoader
    }

    override fun onAdLoadError(error: AdError) {
        // implementation in BannerLoader
        onAdDisplayFailed(error)
    }

    override fun onAdDisplayFailed(error: AdError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdDisplayed() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun destroy() {
        super.destroy()
        CASHandler.main {
            adView.destroy()
        }
    }
}