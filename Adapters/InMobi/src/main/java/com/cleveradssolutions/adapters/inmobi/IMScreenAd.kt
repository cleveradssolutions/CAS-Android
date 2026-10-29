package com.cleveradssolutions.adapters.inmobi

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import com.inmobi.ads.InMobiInterstitial

internal class IMScreenAd(
    val view: InMobiInterstitial
) : MediationAdBase(), MediationScreenAd {

    override fun showScreen(listener: MediationAdListener) {
        if (view.isReady()) {
            view.show()
        } else {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }
}