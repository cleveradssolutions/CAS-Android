package com.cleveradssolutions.adapters.bluesea

import com.blueseasx.sdk.AdError
import com.blueseasx.sdk.BSXAdView
import com.blueseasx.sdk.BSXAdViewListener
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError as CASError

internal class BlueSeaBannerLoader(
    request: MediationBannerAdRequest,
    private var view: BSXAdView
) : BlueSeaBidLoader<BSXAdView>(request), BSXAdViewListener {

    override fun onAdLoaded() {
        val request = request?.forBannerAd() ?: return
        val loadedSize = view.adSize
        if (loadedSize.width > request.adSize.width || loadedSize.height > request.adSize.height) {
            this.request = null
            request.onFailure(
                CASError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Loaded invalid ad size: " + "${loadedSize.width}x${loadedSize.height}"
                )
            )
            view.destroy()
            return
        }

        onAdLoaded(view)
    }

    override fun onAdDisplayFailed(error: AdError) {
        onAdLoadError(error)
    }

    override fun onAdDisplayed() {
    }

    override fun onAdClicked() {
    }
}