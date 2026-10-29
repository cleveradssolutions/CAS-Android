package com.cleveradssolutions.adapters.vungle

import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.vungle.ads.BidTokenCallback
import com.vungle.ads.VungleAds

internal class LiftoffSignalCollector(val request: MediationAdSignalRequest) : BidTokenCallback {

    fun collect() {
        // Request bid token for each request
        VungleAds.getBiddingToken(request.context, this)
    }

    override fun onBidTokenCollected(bidToken: String) {
        request.onSuccess(bidToken)
    }

    override fun onBidTokenError(errorMessage: String) {
        request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, errorMessage))
    }
}