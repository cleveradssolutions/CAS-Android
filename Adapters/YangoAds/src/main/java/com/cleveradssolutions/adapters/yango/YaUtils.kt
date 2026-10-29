package com.cleveradssolutions.adapters.yango

import android.content.res.Configuration
import android.util.Log
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdInfo
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.AdTargeting
import com.yandex.mobile.ads.common.AdTheme
import com.yandex.mobile.ads.common.Gender
import com.yandex.mobile.ads.common.ImpressionData
import org.json.JSONObject

internal var yangoTheme: AdTheme? = null

internal fun MediationBannerAdRequest.getYaBannerSize(): BannerAdSize =
    if (adSize.isAdaptive || adSize.isInline)
        BannerAdSize.inline(context, adSize.width, adSize.height)
    else
        BannerAdSize.fixed(context, adSize.width, adSize.height)

internal fun MediationAdUnitRequest.toYaRequest(): AdRequest {
    val builder = AdRequest.Builder(unitId)

    bidResponse?.let {
        builder.setBiddingData(it)
    }

    yangoTheme?.let {
        builder.setPreferredTheme(it)
    }

    builder.setTargeting(createYaTargeting())

    return builder.build()
}

internal fun createYaTargeting(): AdTargeting {
    val builder = AdTargeting.Builder()
    val targeting = CAS.targetingOptions

    if (targeting.age > 0) {
        builder.setAge(targeting.age.toString())
    }

    if (targeting.gender == TargetingOptions.GENDER_MALE) {
        builder.setGender(Gender.MALE)
    } else if (targeting.gender == TargetingOptions.GENDER_FEMALE) {
        builder.setGender(Gender.FEMALE)
    }

    targeting.location?.let { location ->
        builder.setLocation(location)
    }

    targeting.keywords?.let { keywords ->
        if (keywords.isNotEmpty()) {
            builder.setContextTags(keywords.toList())
        }
    }

    return builder.build()
}

internal fun MediationAdRequest.onYaFailure(error: AdRequestError) {
    onFailure(
        when (error.code) {
            AdRequestError.Code.NO_FILL -> AdError.NO_FILL
            AdRequestError.Code.NETWORK_ERROR -> AdError.NO_CONNECTION
            AdRequestError.Code.INVALID_REQUEST ->
                AdError(AdErrorCode.CONFIGURATION_ERROR, error.description)

            else -> AdError(AdErrorCode.INTERNAL_ERROR, error.description)
        }
    )
}

/**
 * App open ads can only be placed in an application with a vertical orientation.
 * For horizontal orientation, the ads will not be selected.
 */
internal fun MediationAdRequest.requirePortraitOrientationOnly(): Boolean {
    val orientation = context.resources.configuration.orientation
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        onFailure(
            AdError(
                AdErrorCode.REJECTED,
                "Yandex Ads support Portrait orientation only"
            )
        )
        return false
    }
    return true
}

internal fun AdInfo.findCreativeId(): String? {
    for (creative in creatives) {
        creative.creativeId?.let {
            return it
        }
    }
    return null
}

internal fun MediationAd.onYaImpression(data: ImpressionData?) {
    val callback = listener ?: return
    /**
     * For example: {
     * "currency":"RUB",
     * "revenueUSD":"0.000112202",
     * "precision":"estimated",
     * "revenue":"0.007727661",
     * "requestId":"16-15-production-app-host",
     * "blockId":"R-M-",
     * "adType":"interstitial",
     * "ad_unit_id":"R-M-",
     * "network":{"name":"Yandex","adapter":"Yandex","ad_unit_id":"R-M-"}
     * }
     */
    try {
        if (data != null && data.rawData.isNotEmpty()) {
            val json = JSONObject(data.rawData)
            // Revenue per impression converted to USD.
            val revenue = json.optDouble("revenueUSD")
            if (!revenue.isNaN()) {
                costPerMille = revenue * 1000.0
                revenuePrecision = if (json.optString("precision") == "estimated")
                    AdRevenuePrecision.ESTIMATED
                else
                    AdRevenuePrecision.FLOOR
            }
        }
    } catch (e: Throwable) {
        callback.debug("Impression data error", this, e, Log.ERROR)
    }
    callback.onAdImpression(this)
}