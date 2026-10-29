package com.cleveradssolutions.adapters.maticoo

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.zmaticoo.sdk.flow.model.ComponentError


internal fun ComponentError.toCASError(): AdError {
    return when (errorCode) {
        ComponentError.LOAD_FAILED_PLACEMENT_NOT_FOUND,
        ComponentError.INIT_FAILED_INVALID_CONFIGURATION ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        ComponentError.LOAD_FAILED_NETWORK_ERROR,
        ComponentError.SHOW_FAILED_NETWORK_UNREACHABLE -> AdError(AdErrorCode.NO_CONNECTION, message)

        ComponentError.LOAD_FAILED_NO_FILL -> AdError.NO_FILL

        ComponentError.LOAD_FAILED_SDK_NOT_INITIALIZED,
        ComponentError.SHOW_FAILED_SDK_NOT_INITIALIZED -> AdError.NOT_INITIALIZED

        ComponentError.SHOW_FAILED_AD_NOT_READY,
        ComponentError.SHOW_FAILED_AD_NOT_IS_READY -> AdError.NOT_READY

        ComponentError.SHOW_FAILED_MISSING_ACTIVITY -> AdError(AdErrorCode.NOT_FOREGROUND)

        else -> AdError(AdErrorCode.INTERNAL_ERROR, message)
    }
}
