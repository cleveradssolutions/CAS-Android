package com.cleveradssolutions.adapters.hyprmx

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.mediation.core.MediationSingleAdBase
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.hyprmx.android.sdk.core.HyprMXErrors
import com.hyprmx.android.sdk.placement.HyprMXLoadAdListener
import com.hyprmx.android.sdk.placement.HyprMXPlacementExpiryListener
import com.hyprmx.android.sdk.placement.HyprMXRewardedShowListener
import com.hyprmx.android.sdk.placement.HyprMXShowListener
import com.hyprmx.android.sdk.placement.Placement

internal class HMXScreenAd(
    private var placement: Placement,
    request: MediationScreenAdRequest
) : MediationSingleAdBase(), MediationScreenAd, MediationAdLoader,
    HyprMXLoadAdListener, HyprMXShowListener,
    HyprMXRewardedShowListener, HyprMXPlacementExpiryListener {

    private var request: MediationScreenAdRequest? = request

    init {
        val bidResponse = request.bidResponse
        if (bidResponse != null)
            placement.loadAd(bidResponse, this)
        else
            placement.loadAd(this)
    }

    override fun showScreen(listener: MediationAdListener) {
        if (placement.isAdAvailable())
            placement.showAd(this)
        else
            listener.onAdFailedToShow(this, AdError.NOT_READY)
    }

    override fun destroy() {
        super.destroy()
        placement.setPlacementExpiryListener(null)
    }

    override fun onAdLoaded(isAdAvailable: Boolean) {
        val request = request ?: return
        this.request = null
        if (isAdAvailable) {
            placement.setPlacementExpiryListener(this)
            request.onSuccess(this)
        } else {
            request.onFailure(AdError.NO_FILL)
        }
    }

    override fun onAdStarted(placement: Placement) {
        listener?.onAdShowed(this)
    }

    override fun onAdDisplayError(placement: Placement, hyprMXError: HyprMXErrors) {
        listener?.onAdFailedToShow(
            this,
            AdError(AdErrorCode.INTERNAL_ERROR, hyprMXError.toString())
        )
    }

    override fun onAdImpression(placement: Placement) {
        listener?.onAdImpression(this)
    }

    override fun onAdRewarded(placement: Placement, rewardName: String, rewardValue: Int) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdClosed(placement: Placement, finished: Boolean) {
        listener?.onAdDismissed(this)
    }

    override fun onAdExpired(placement: Placement) {
        setAdExpired()
    }
}