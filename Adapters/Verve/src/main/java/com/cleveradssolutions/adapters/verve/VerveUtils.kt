package com.cleveradssolutions.adapters.verve

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.HyBidErrorCode

internal fun Throwable?.toVerveAdError(): AdError =
    if (this == null || this !is HyBidError)
        AdError(AdErrorCode.INTERNAL_ERROR)
    else when (errorCode) {
        HyBidErrorCode.NO_FILL,
        HyBidErrorCode.NULL_AD,
        HyBidErrorCode.AUCTION_NO_AD -> AdError.NO_FILL

        HyBidErrorCode.INVALID_ASSET,
        HyBidErrorCode.DISABLED_FORMAT,
        HyBidErrorCode.INVALID_AD,
        HyBidErrorCode.INVALID_ZONE_ID ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        HyBidErrorCode.NOT_INITIALISED -> AdError.NOT_INITIALIZED
        HyBidErrorCode.EXPIRED_AD -> AdError.EXPIRED
        else -> AdError(AdErrorCode.INTERNAL_ERROR, message)
    }
