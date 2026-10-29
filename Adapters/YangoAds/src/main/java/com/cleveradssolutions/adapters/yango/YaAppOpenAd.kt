package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.yandex.mobile.ads.appopenad.AppOpenAd
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.AdError as YaAdError

internal class YaAppOpenAd(val view: AppOpenAd) :
    MediationAdBase(),
    MediationScreenAd, AppOpenAdEventListener {

    init {
        creativeId = view.adInfo.findCreativeId()
    }

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        view.setAdEventListener(this)
        view.show(activity)
    }

    override fun onAdFailedToShow(adError: YaAdError) {
        listener?.onAdFailedToShow(this, AdError(AdErrorCode.INTERNAL_ERROR, adError.description))
    }

    override fun onAdImpression(impressionData: ImpressionData?) {
        onYaImpression(impressionData)
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
}