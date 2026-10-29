package com.cleveradssolutions.adapters.displayio

import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.exceptions.DioErrorCode
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError

internal fun DIOError.toCASError(): AdError = when (errorCode) {
    DioErrorCode.ErrorNoFill,
    DioErrorCode.ErrorNoAds,
    DioErrorCode.ErrorNoDataInResponse -> AdError.NO_FILL

    DioErrorCode.ErrorNoPlacementsSectionInResponse,
    DioErrorCode.ErrorUnknownPlacementType,
    DioErrorCode.ErrorUnsupportedPlatform ->
        AdError(AdErrorCode.CONFIGURATION_ERROR, message)

    DioErrorCode.ErrorLoading -> AdError.NO_CONNECTION
    else -> AdError(message)
}
