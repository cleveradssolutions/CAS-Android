package com.cleveradssolutions.adapters.ysonetwork

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.ysocorp.ysonetwork.enums.YNEnumActionError

internal fun casErrorFromYso(error: YNEnumActionError?) = when (error) {
    YNEnumActionError.Timeout -> AdError.TIMEOUT
    YNEnumActionError.Server -> AdError.NO_CONNECTION
    YNEnumActionError.SdkNotInitialized -> AdError.NOT_INITIALIZED
    YNEnumActionError.InvalidConfig -> AdError(AdErrorCode.CONFIGURATION_ERROR)
    YNEnumActionError.InvalidRequest -> AdError(
        AdErrorCode.INTERNAL_ERROR,
        "Invalid request"
    )

    else -> AdError(AdErrorCode.INTERNAL_ERROR)
}
