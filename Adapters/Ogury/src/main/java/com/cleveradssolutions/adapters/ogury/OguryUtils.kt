package com.cleveradssolutions.adapters.ogury

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.ogury.ad.OguryLoadErrorCode
import com.ogury.ad.OguryShowErrorCode
import com.ogury.core.OguryError
import com.ogury.sdk.OguryStartErrorCode

internal fun OguryError.toCASError(): AdError =
    when (code) {
        OguryLoadErrorCode.NO_FILL -> AdError.NO_FILL
        OguryLoadErrorCode.NO_ACTIVE_INTERNET_CONNECTION,
        OguryShowErrorCode.NO_ACTIVE_INTERNET_CONNECTION -> AdError.NO_CONNECTION

        OguryLoadErrorCode.AD_DISABLED_CONSENT_DENIED,
        OguryLoadErrorCode.AD_DISABLED_CONSENT_MISSING,
        OguryLoadErrorCode.AD_DISABLED_COUNTRY_NOT_OPENED ->
            AdError(AdErrorCode.REJECTED, message)

        OguryLoadErrorCode.INVALID_CONFIGURATION,
        1001, // bundle mismatch
        OguryStartErrorCode.MODULE_MISSING ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        OguryLoadErrorCode.SDK_NOT_STARTED,
        OguryLoadErrorCode.SDK_NOT_PROPERLY_INITIALIZED,
        OguryShowErrorCode.SDK_NOT_STARTED ->
            AdError(AdErrorCode.NOT_INITIALIZED, message)

        OguryShowErrorCode.AD_EXPIRED -> AdError.EXPIRED
        OguryShowErrorCode.NO_AD_LOADED -> AdError.NOT_READY
        OguryShowErrorCode.ANOTHER_AD_ALREADY_DISPLAYED -> AdError(AdErrorCode.ALREADY_DISPLAYED)
        OguryShowErrorCode.VIEW_IN_BACKGROUND -> AdError(AdErrorCode.NOT_FOREGROUND)
        else -> AdError(AdErrorCode.INTERNAL_ERROR, message)
    }