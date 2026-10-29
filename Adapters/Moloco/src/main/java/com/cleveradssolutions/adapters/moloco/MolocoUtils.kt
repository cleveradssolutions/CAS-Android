package com.cleveradssolutions.adapters.moloco

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.MolocoAdError

internal fun MolocoAdError.toCASError(): AdError {
    val message = description
    return when (errorType) {
        MolocoAdError.ErrorType.SDK_INIT_ERROR,
        MolocoAdError.ErrorType.AD_LOAD_FAILED_SDK_NOT_INIT ->
            AdError.NOT_INITIALIZED

        MolocoAdError.ErrorType.SDK_INVALID_CONFIGURATION ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        MolocoAdError.ErrorType.AD_LOAD_TIMEOUT_ERROR ->
            AdError.NO_CONNECTION

        MolocoAdError.ErrorType.AD_SHOW_ERROR_NOT_LOADED ->
            AdError.NOT_READY

        MolocoAdError.ErrorType.AD_SHOW_ERROR_ALREADY_DISPLAYING ->
            AdError(AdErrorCode.ALREADY_DISPLAYED)
        else ->
            AdError(AdErrorCode.INTERNAL_ERROR, message)
    }
}

internal fun MolocoAdError.AdCreateError?.toCASError(): AdError {
    return when (this) {
        MolocoAdError.AdCreateError.SDK_INIT_FAILED,
        MolocoAdError.AdCreateError.SDK_INIT_WAS_NOT_COMPLETED ->
            AdError.NOT_INITIALIZED

        MolocoAdError.AdCreateError.UNABLE_TO_CREATE_AD ->
            AdError(AdErrorCode.INTERNAL_ERROR, description)

        null ->
            AdError(AdErrorCode.INTERNAL_ERROR, "Moloco failed to create ad")
    }
}