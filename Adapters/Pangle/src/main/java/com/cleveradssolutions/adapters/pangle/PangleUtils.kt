package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.PangleAd
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerSize
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.core.MediationAdRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError

private const val NOT_INITIALIZED = 10000
private const val NO_FILL = 20001

private const val CONTENT_TYPE = 40000
private const val REQUEST_PB_ERROR = 40001
private const val EMPTY_APP_ID = 40002
private const val WAP_EMPTY = 40003
private const val EMPTY_AD_SLOT_ID = 40004
private const val INVALID_AD_SLOT_SIZE = 40005
private const val INVALID_AD_SLOT_ID = 40006
private const val LOAD_AD_COUNT_ERROR = 40007
private const val INVALID_IMAGE_SIZE = 40008
private const val INVALID_APP_ID = 40009
private const val ERROR_MEDIA_TYPE = 40010
private const val ERROR_AD_TYPE = 40011
private const val ERROR_ACCESS_METHOD_PASS = 40012
private const val WRONG_SPLASH_AD_TYPE = 40013
private const val ERROR_REDIRECT = 40014
private const val ERROR_REQUEST_INVALID = 40015
private const val NOT_MATCH_SLOT_AND_APP_IDS = 40016
private const val ERROR_ACCESS_METHOD_API_SDK = 40017
private const val INVALID_PACKAGE_NAME = 40018
private const val INVALID_FORMAT_FOR_SLOT = 40019
private const val ERROR_NEW_REGISTER_LIMIT = 40020
private const val ERROR_APK_SIGN_CHECK_ERROR = 40021
private const val INCORRECT_BID_API_METHOD = 40022
private const val ERROR_UNION_OS_ERROR = 40023
private const val ERROR_UNION_SDK_TOO_OLD = 40024
private const val ERROR_UNION_SDK_NOT_INSTALLED = 40025
private const val INCORRECT_BID_API_METHOD_2 = 40029
private const val INVALID_BID_PLACEMENT = 40034

// Fake error for first session: Appid is not registered on pangle media platform (c40060)
private const val APP_ID_NOT_REGISTERED = 40060

private const val SERVER_ERROR = 50001

private const val SHOW_EVENT_ERROR = 60001
private const val CLICK_EVENT_ERROR = 60002
private const val ERROR_VERIFY_REWARD = 60007

private const val PARSE_FAIL = -1
private const val NET_ERROR = -2

private const val NO_AD_PARSE = -3
private const val AD_DATA_ERROR = -4
private const val BANNER_AD_LOAD_IMAGE_ERROR = -5
private const val INSERT_AD_LOAD_IMAGE_ERROR = -6
private const val SPLASH_AD_LOAD_IMAGE_ERROR = -7
private const val FREQUENT_CALL_ERROR = -8
private const val REQUEST_BODY_ERROR = -9
private const val SPLASH_CACHE_PARSE_ERROR = -10
private const val SPLASH_CACHE_EXPIRED_ERROR = -11
private const val SPLASH_NOT_HAVE_CACHE_ERROR = -12

private const val PROHIBIT_REQUESTING_ADS = -16

// Please update to the latest sdk when:
private const val FAIL_PARSE_RENDERING_RESULT_DATA_ERROR = 101
private const val INVALID_MAIN_TEMPLATE_ERROR = 102
private const val INVALID_TEMPLATE_DIFFERENCE_ERROR = 103
private const val ABNORMAL_MATERIAL_DATA_ERROR = 104
private const val TEMPLATE_DATA_PARSING_ERROR = 105
private const val RENDERING_ERROR = 106
private const val RENDERING_TIMEOUT_ERROR = 107
private const val RENDERING_TIMEOUT_ERROR_2 = 108
private const val TEMPLATE_LOADS_FAILED = 109


// Check network status and request ads again when:
private const val LINK_ESTABLISHMENT_FAILED = 601
private const val LINK_ESTABLISHMENT_TIMEOUT = 602

internal const val KEY_PAG_USER_DATA = "pag_config_user_data"
internal const val KEY_PAG_ADX = "pag_adx_id"

internal fun MediationAdRequest.applyPAGConfig() {
    val data = getStrParameter(KEY_PAG_USER_DATA) ?: return
    //?: "[{\"name\":\"mediation\",\"value\":\"google\"},{\"name\":\"adapter_version\",\"value\":\"${BuildConfig.VERSION}\"}]"
    PAGConfig.setUserData(data)
}

internal fun MediationAdRequest.debugExtraInfo(ad: PangleAd) {
    // Extras example:
    // price = 99
    // sdk_bidding_type = 2
    // nurl = ...
    // lurl = ...
    // tag_id = b4e23dbc1c830c00bde3fc1cb729ed19
    // request_id = 26004451-9332-402c-9b09-9def2d209251u6021

    if (BuildConfig.DEBUG) {
        for (info in ad.mediaExtraInfo) {
            debug("(D) " + info.key + ": " + info.value?.toString())
        }
    }
}

internal fun MediationBannerAdRequest.getPAGSize(): PAGBannerSize =
    if (adSize.isAdaptive) {
        PAGBannerSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adSize.width)
    } else if (adSize.isInline) {
        PAGBannerSize.getInlineAdaptiveBannerAdSize(adSize.width, adSize.height)
    } else when (adSizeId) {
        AdSizeId.LARGE -> PAGBannerSize.BANNER_W_728_H_90
        AdSizeId.MREC -> PAGBannerSize.BANNER_W_300_H_250
        else -> PAGBannerSize.BANNER_W_320_H_50
    }

internal fun MediationAdUnitRequest?.onPaFailure(code: Int, message: String?) {
    this?.onFailure(toCASError(code, message))
}

internal fun toCASError(code: Int, message: String?): AdError = when (code) {
    NO_FILL,
    PROHIBIT_REQUESTING_ADS ->
        AdError.NO_FILL

    SPLASH_CACHE_EXPIRED_ERROR ->
        AdError.EXPIRED

    NET_ERROR,
    LINK_ESTABLISHMENT_FAILED,
    LINK_ESTABLISHMENT_TIMEOUT ->
        AdError.NO_CONNECTION

    NOT_INITIALIZED ->
        AdError.NOT_INITIALIZED

    else -> if (code in 40000..40034)
        AdError(AdErrorCode.CONFIGURATION_ERROR, "$message (c$code)")
    else
        AdError(AdErrorCode.INTERNAL_ERROR, "$message (c$code)")
}

