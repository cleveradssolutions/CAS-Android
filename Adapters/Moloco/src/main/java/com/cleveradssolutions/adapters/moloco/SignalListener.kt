package com.cleveradssolutions.adapters.moloco

import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.MolocoAdError
import com.moloco.sdk.publisher.MolocoBidTokenListener

internal class SignalListener(
    private val request: MediationAdSignalRequest
) : MolocoBidTokenListener {
    override fun onBidTokenResult(
        bidToken: String,
        error: MolocoAdError.ErrorType?
    ) {
        if (error != null) {
            request.onFailure(AdError(error.description))
        } else {
            request.onSuccess(bidToken)
        }
    }
}