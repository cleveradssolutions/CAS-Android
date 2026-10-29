package com.cleveradssolutions.adapters.facebook

import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.facebook.ads.Ad
import com.facebook.ads.ExtraHints

typealias FacebookError = com.facebook.ads.AdError

internal fun FacebookError.toCASError(): AdError =
    when (errorCode) {
        FacebookError.NETWORK_ERROR_CODE -> AdError.NO_CONNECTION
        FacebookError.NO_FILL_ERROR_CODE -> AdError.NO_FILL
        FacebookError.SHOW_CALLED_BEFORE_LOAD_ERROR_CODE -> AdError.NOT_READY
        FacebookError.CLEAR_TEXT_SUPPORT_NOT_ALLOWED ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, errorMessage)
        else -> AdError(AdErrorCode.INTERNAL_ERROR, errorMessage)
    }

@Suppress("DEPRECATION")
internal fun Ad.setWatermark(request: MediationAdUnitRequest) {
    request.getStrParameter("meta_watermark")?.let {
        setExtraHints(
            ExtraHints.Builder().mediationData(it).build()
        )
    }
}
