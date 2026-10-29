package com.cleveradssolutions.adapters.vungle

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.vungle.ads.VungleError
import com.vungle.ads.internal.protos.Sdk.SDKError.Reason


internal fun VungleError.toCASError(): AdError =
    when (code) {
        Reason.PLACEMENT_SLEEP_VALUE,
        Reason.AD_NO_FILL_VALUE,
        Reason.AD_LOAD_TOO_FREQUENTLY_VALUE -> AdError.NO_FILL

        Reason.AD_NOT_LOADED_VALUE -> AdError.NOT_READY
        Reason.AD_EXPIRED_VALUE, Reason.AD_EXPIRED_ON_PLAY_VALUE -> AdError.EXPIRED

        Reason.INVALID_APP_ID_VALUE,
        Reason.INVALID_PLACEMENT_ID_VALUE,
        Reason.PLACEMENT_AD_TYPE_MISMATCH_VALUE,
        Reason.BANNER_VIEW_INVALID_SIZE_VALUE,
        Reason.AD_PUBLISHER_MISMATCH_VALUE ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, errorMessage)

        Reason.SDK_NOT_INITIALIZED_VALUE ->
            AdError.NOT_INITIALIZED

        Reason.AD_RESPONSE_TIMED_OUT_VALUE -> AdError.TIMEOUT

        else -> AdError(AdErrorCode.INTERNAL_ERROR, errorMessage)
    }
