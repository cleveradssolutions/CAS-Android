package com.cleveradssolutions.adapters.verve

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd

internal class VerveRewardedAd(
    request: MediationRewardedAdRequest
) : MediationAdBase(), MediationScreenAd, HyBidRewardedAd.Listener, MediationAdLoader {

    private var ad: HyBidRewardedAd? = null
    private var request: MediationRewardedAdRequest? = request

    init {
        val bid = request.bidResponse
        if (bid != null) {
            ad = HyBidRewardedAd(request.context, "", this)
            ad?.prepareAd(bid)
        } else {
            val appToken = HyBid.getAppToken()
            ad = HyBidRewardedAd(request.context, appToken, request.unitId, this)
            ad?.load()
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        ad?.let {
            if (it.isReady) {
                it.show()
                return
            }
        }
        listener.onAdFailedToShow(this, AdError.NOT_READY)
    }

    override fun destroy() {
        super.destroy()
        ad?.destroy()
        ad = null
    }

    override fun onRewardedLoaded() {
        creativeId = ad?.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        request?.onFailure(error.toVerveAdError())
        request = null
    }

    override fun onRewardedShowFailed(error: Throwable?) {
        listener?.onAdFailedToShow(this, error.toVerveAdError())
    }

    override fun onRewardedOpened() {
        listener?.onAdImpression(this)
    }

    override fun onRewardedClick() {
        listener?.onAdClicked(this)
    }

    override fun onReward() {
        listener?.onUserEarnedReward(this)
    }

    override fun onRewardedClosed() {
        listener?.onAdDismissed(this)
    }
}
