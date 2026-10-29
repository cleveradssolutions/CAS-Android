package com.cleveradssolutions.adapters.bigo

import com.cleveradssolutions.internal.scope
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.android.CAS
import org.json.JSONStringer
import sg.bigo.ads.api.AdError

internal typealias CASError = com.cleversolutions.ads.AdError

private var casExtensionString: String = ""

internal fun MediationAdUnitRequest.getMediationExtString(): String {
    getParameter("mediation_ext")?.let {
        return it.toString()
    }
    if (casExtensionString.isEmpty()) {
        casExtensionString = JSONStringer().scope {
            key("mediationName").value("cas")
            key("mediationVersion").value(CAS.getSDKVersion())
            key("adapterVersion").value(BuildConfig.VERSION)
        }.toString()
    }
    return casExtensionString
}

internal fun AdError.toCASError() = when (code) {
    AdError.ERROR_CODE_APP_ID_UNMATCHED,
        // Indicates that this ad request is invalid.
        // Please check whether your App Id or Slot Id is correctly passed in.
        // For banner ads, please make sure to pass in at least one AdSize.
    AdError.ERROR_CODE_INVALID_REQUEST,
        // Indicates that the current ad Slot Id is unavailable,
        // which is usually caused by the inactive status of your Slot Id,
        // please check it on Bigo Ads platform
    AdError.ERROR_CODE_AD_DISABLE,
        // App id cannot be empty, please pass the id when initializing bigo sdk
    1013,
        // The slot id is inactive or invalid, please make sure the id is aligned
        // with app id. If ids are correct, please wait for at least 30 minutes
        // then try again
    1014,
        // The switch of the slot is turned off. Please check slot setup.
    1015,
        // The ad type of this slot isn't consistent with the method to querying an ad.
    1016,
        // An adm show be passed when constructing an ad request if using a server bidding slot.
    1018,
        // Please pass slot id when constructing an ad request
    1019,
        // App id cannot be empty, please pass the id when initializing bigo sdk
    1100 -> if (message.contains("Missing GDPR consent"))
        CASError(AdErrorCode.REJECTED, message)
    else
        CASError(AdErrorCode.CONFIGURATION_ERROR, "$message (c$code)")

    AdError.ERROR_CODE_NETWORK_ERROR,
    AdError.ERROR_CODE_TIMEOUT_STRATEGY,
        // Request timeout
    1025 ->
        CASError.NO_CONNECTION

    AdError.ERROR_CODE_NO_FILL ->
        CASError.NO_FILL

    AdError.ERROR_CODE_AD_EXPIRED ->
        CASError.EXPIRED

    // The impressions of the ad has reached the limit. You can change this setup on bigo's console
    1017 -> CASError.REACHED_CAP

    // AdError.ERROR_CODE_UNINITIALIZED
    // AdError.ERROR_CODE_INTERNAL_ERROR
    // AdError.ERROR_CODE_NATIVE_VIEW_MISSING
    // AdError.ERROR_CODE_VIDEO_ERROR
    // AdError.ERROR_CODE_ACTIVITY_CREATE_ERROR
    // ERROR_CODE_ASSETS_ERROR
    // AdError.ERROR_CODE_FULLSCREEN_AD_FAILED_TO_SHOW
    // AdError.ERROR_CODE_FULLSCREEN_AD_FAILED_TO_OPEN

    // Error from server: slot not found placement_id in adunit (c1005)
    else -> CASError(AdErrorCode.INTERNAL_ERROR, "$message (c$code)")
}