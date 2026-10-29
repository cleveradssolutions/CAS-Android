package com.cleveradssolutions.adapters.ogury

import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.ogury.ad.OguryBidTokenErrorCode
import com.ogury.ad.OguryBidTokenListener
import com.ogury.ad.OguryBidTokenProvider
import com.ogury.core.OguryError

internal class OSignalCollector(val request: MediationAdSignalRequest) : OguryBidTokenListener {

    override fun onBidTokenGenerated(bidToken: String) {
        request.onSuccess(bidToken)
    }

    override fun onBidTokenGenerationFailed(error: OguryError) {
        request.onFailure(
            when (error.code) {
                OguryBidTokenErrorCode.SDK_NOT_STARTED -> AdError.NOT_INITIALIZED
                OguryBidTokenErrorCode.INVALID_CONFIGURATION ->
                    AdError(AdErrorCode.CONFIGURATION_ERROR, error.message)

                OguryBidTokenErrorCode.SDK_NOT_PROPERLY_INITIALIZED ->
                    AdError(AdErrorCode.NOT_INITIALIZED, error.message)

                else -> AdError(AdErrorCode.INTERNAL_ERROR, error.message)
            }
        )
    }
}