package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.common.AdError as YaAdError

internal class YaInterstitialAd(val ad: InterstitialAd) :
    MediationAdBase(), MediationScreenAd,
    InterstitialAdEventListener {

    init {
        creativeId = ad.adInfo.findCreativeId()
    }

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        ad.setAdEventListener(this)
        ad.show(activity)
    }

    override fun onAdShown() {
        listener?.onAdShowed(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdDismissed() {
        listener?.onAdDismissed(this)
    }

    override fun onAdFailedToShow(adError: YaAdError) {
        listener?.onAdFailedToShow(this, AdError(AdErrorCode.INTERNAL_ERROR, adError.description))
    }

    override fun onAdImpression(impressionData: ImpressionData?) {
        onYaImpression(impressionData)
    }
}