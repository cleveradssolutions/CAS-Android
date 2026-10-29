package com.cleveradssolutions.adapters.unity

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.unity3d.ads.UnityAdsError
import gatewayprotocol.v1.ErrorOuterClass.PublicErrorCode

private const val UNITY_LOAD_NO_FILL = PublicErrorCode.PUBLIC_ERROR_CODE_LOAD_NO_FILL_VALUE // 52100
private const val UNITY_LOAD_NOT_INITIALIZED =
    PublicErrorCode.PUBLIC_ERROR_CODE_LOAD_NOT_INITIALIZED_VALUE // 52101
private const val UNITY_LOAD_INVALID_ARGUMENT =
    PublicErrorCode.PUBLIC_ERROR_CODE_LOAD_PLACEMENT_NOT_FOUND_VALUE // 52102
private const val UNITY_INVALID_CONFIGURATION =
    PublicErrorCode.PUBLIC_ERROR_CODE_LOAD_UNSUPPORTED_PLACEMENT_VALUE // 52104
private const val UNITY_LOAD_NETWORK = PublicErrorCode.PUBLIC_ERROR_CODE_LOAD_NETWORK_VALUE // 52105
private const val UNITY_INIT_NETWORK = PublicErrorCode.PUBLIC_ERROR_CODE_INIT_NETWORK_VALUE // 52005
private const val UNITY_INIT_MISMATCHED_PLATFORM =
    PublicErrorCode.PUBLIC_ERROR_CODE_INIT_MISMATCHED_PLATFORM_VALUE // 52002
private const val UNITY_INIT_NOT_FOUND =
    PublicErrorCode.PUBLIC_ERROR_CODE_INIT_NOT_FOUND_VALUE // 52001
private const val UNITY_SHOW_EXPIRED = PublicErrorCode.PUBLIC_ERROR_CODE_SHOW_EXPIRED_VALUE //52200
private const val UNITY_SHOW_ALREADY =
    PublicErrorCode.PUBLIC_ERROR_CODE_SHOW_ALREADY_SHOWN_VALUE //52201

private const val UNITY_TIMEOUT = PublicErrorCode.PUBLIC_ERROR_CODE_TIMEOUT_VALUE //2

internal fun mapUnityError(error: UnityAdsError?): AdError {
    if (error == null) return AdError(AdErrorCode.INTERNAL_ERROR)

    return when (error.code) {
        UNITY_LOAD_NO_FILL -> AdError.NO_FILL
        UNITY_TIMEOUT,
        UNITY_INIT_NETWORK,
        UNITY_LOAD_NETWORK -> AdError.NO_CONNECTION

        UNITY_LOAD_NOT_INITIALIZED -> AdError.NOT_INITIALIZED

        UNITY_LOAD_INVALID_ARGUMENT,
        UNITY_INVALID_CONFIGURATION,
        UNITY_INIT_MISMATCHED_PLATFORM,
        UNITY_INIT_NOT_FOUND ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, error.message)

        UNITY_SHOW_EXPIRED -> AdError.EXPIRED
        UNITY_SHOW_ALREADY -> AdError(AdErrorCode.ALREADY_DISPLAYED)
        else -> AdError(error.message)
    }
}