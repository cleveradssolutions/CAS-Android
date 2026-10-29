package com.cleveradssolutions.adapters.ogury

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import com.ogury.ad.OguryAdError
import com.ogury.ad.OguryReward
import com.ogury.ad.OguryRewardedAd
import com.ogury.ad.OguryRewardedAdListener
import com.ogury.ad.common.OguryMediation

internal class ORewardedAd(request: MediationRewardedAdRequest, mediation: OguryMediation) : MediationAdBase(),
    OguryRewardedAdListener, MediationScreenAd, MediationAdLoader {
    private var view = OguryRewardedAd(request.context, request.unitId, mediation)
    private var request: MediationRewardedAdRequest? = request

    init {
        view.setListener(this)
        if (view.isLoaded()) {
            request.onSuccess(this)
        } else {
            view.load(request.bidResponse)
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        if (view.isLoaded()) {
            view.show()
            listener.onAdShowed(this)
        } else {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }

    override fun onAdLoaded(ad: OguryRewardedAd) {
        request?.onSuccess(this)
        request = null
    }

    override fun onAdError(ad: OguryRewardedAd, error: OguryAdError) {
        if (error.type == OguryAdError.Type.SHOW_ERROR) {
            listener?.onAdFailedToShow(this, error.toCASError())
        } else {
            request?.onFailure(error.toCASError())
            request = null
        }
    }

    override fun onAdClicked(ad: OguryRewardedAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdClosed(ad: OguryRewardedAd) {
        listener?.onAdDismissed(this)
    }

    override fun onAdImpression(ad: OguryRewardedAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdRewarded(ad: OguryRewardedAd, reward: OguryReward) {
        listener?.onUserEarnedReward(this)
    }
}