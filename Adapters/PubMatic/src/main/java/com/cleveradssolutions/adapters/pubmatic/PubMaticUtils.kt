package com.cleveradssolutions.adapters.pubmatic

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.pubmatic.sdk.common.POBError

internal fun POBError.toCASError(): AdError {
    return when (errorCode) {
        POBError.NO_ADS_AVAILABLE -> AdError.NO_FILL
        POBError.NETWORK_ERROR -> AdError.NO_CONNECTION
        POBError.AD_NOT_READY -> AdError.NOT_READY
        POBError.AD_EXPIRED -> AdError.EXPIRED
        POBError.AD_ALREADY_SHOWN ->
            AdError(AdErrorCode.ALREADY_DISPLAYED, errorMessage)

        POBError.AD_REQUEST_NOT_ALLOWED,
        POBError.INVALID_REQUEST,
        POBError.INVALID_CONFIG,
        POBError.INVALID_REWARD_SELECTED,
        POBError.REWARD_NOT_SELECTED ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, errorMessage)

        else -> AdError(AdErrorCode.INTERNAL_ERROR, errorMessage)
    }
}


