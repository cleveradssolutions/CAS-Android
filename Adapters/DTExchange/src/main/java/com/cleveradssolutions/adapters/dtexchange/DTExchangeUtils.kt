package com.cleveradssolutions.adapters.dtexchange

import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.fyber.inneractive.sdk.external.ImpressionData
import com.fyber.inneractive.sdk.external.InneractiveErrorCode

internal fun MediationAd.recordImpression(data: ImpressionData) {
    data.creativeId?.let {
        creativeId = it
    }
    if (data.pricing != null && data.pricing.currency == "USD") {
        revenuePrecision = AdRevenuePrecision.ESTIMATED
        costPerMille = data.pricing.value * 1000.0
    }
    listener?.onAdImpression(this)
}

internal fun InneractiveErrorCode?.toCASError(): AdError = when (this) {
    null, InneractiveErrorCode.NO_FILL ->
        AdError.NO_FILL

    InneractiveErrorCode.CONNECTION_TIMEOUT,
    InneractiveErrorCode.CONNECTION_ERROR,
    InneractiveErrorCode.LOAD_TIMEOUT,
    InneractiveErrorCode.IN_FLIGHT_TIMEOUT ->
        AdError.NO_CONNECTION

    InneractiveErrorCode.UNKNOWN_APP_ID,
    InneractiveErrorCode.INVALID_INPUT,
    InneractiveErrorCode.ERROR_CONFIGURATION_MISMATCH,
    InneractiveErrorCode.ERROR_CONFIGURATION_NO_SUCH_SPOT,
    InneractiveErrorCode.SPOT_DISABLED,
    InneractiveErrorCode.UNSUPPORTED_SPOT ->
        AdError(AdErrorCode.CONFIGURATION_ERROR, this.toString())

    InneractiveErrorCode.SDK_NOT_INITIALIZED,
    InneractiveErrorCode.SDK_NOT_INITIALIZED_OR_CONFIG_ERROR ->
        AdError.NOT_INITIALIZED
    //InneractiveErrorCode.ERROR_CODE_NATIVE_VIDEO_NOT_SUPPORTED,
    //InneractiveErrorCode.NATIVE_ADS_NOT_SUPPORTED_FOR_OS,
    //InneractiveErrorCode.SERVER_INTERNAL_ERROR,
    //InneractiveErrorCode.SERVER_INVALID_RESPONSE,
    //InneractiveErrorCode.SDK_INTERNAL_ERROR,
    //InneractiveErrorCode.CANCELLED
    else ->
        AdError(AdErrorCode.INTERNAL_ERROR, this.toString())
}
