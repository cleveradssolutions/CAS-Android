package com.cleveradssolutions.adapters.monetrix

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError

internal typealias MXError = com.monetrix.adsdk.api.base.AdError

internal fun MXError.toCASError(): AdError = when (code) {
    MXError.ErrorNoAdFillNoWin -> AdError.NO_FILL
    MXError.ERROR_CODE_AD_EXPIRED -> AdError.EXPIRED

    MXError.ErrorCoppaRestriction -> AdError(AdErrorCode.REJECTED, message)

    MXError.ErrorSDKNoAppID,
    MXError.ErrorAppInActive,
    MXError.ErrorAdTypeInconsistent,
    MXError.ServerErrorAppInAcive,
    MXError.ServerErrorPidInAcive,
    MXError.ServerErrorPkgNotExist,
    MXError.ServerErrorAppPkgInconsistent,
    MXError.ServerErrorAdtypePidInconsistent ->
        AdError(AdErrorCode.CONFIGURATION_ERROR, message)

    else -> AdError(message)
}
