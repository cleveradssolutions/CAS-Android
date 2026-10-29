package com.cleveradssolutions.adapters.ysonetwork

import android.view.View
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.ysocorp.ysonetwork.YsoNetwork
import com.ysocorp.ysonetwork.enums.YNEnumActionError

internal open class YsoInterstitialAd(request: MediationScreenAdRequest) :
    MediationAdBase(), MediationScreenAd, MediationAdLoader,
    YsoNetwork.ActionLoad, YsoNetwork.ActionDisplay {

    var request: MediationScreenAdRequest? = request
    val auctionId = request.getStrParameter(Constants.KEY_BID_AUCTION_ID)
        ?: request.unitId

    init {
        unitId = request.unitId
        costPerMille = request.floor
        revenuePrecision = AdRevenuePrecision.PRECISE
    }

    final override fun onLoad(error: YNEnumActionError?) {
        val request = request ?: return
        this.request = null
        if (error == YNEnumActionError.None) {
            request.onSuccess(this)
        } else {
            request.onFailure(casErrorFromYso(error))
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        YsoNetwork.interstitialShow(auctionId, this, activity)
    }

    final override fun onDisplay(view: View?) {
        listener?.onAdImpression(this)
    }

    override fun onClick() {
        listener?.onAdClicked(this)
    }

    final override fun onClose(display: Boolean, complete: Boolean) {
        val listener = listener ?: return
        if (display) {
            if (complete) {
                listener.onUserEarnedReward(this)
            }
            listener.onAdDismissed(this)
        } else {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }
}
