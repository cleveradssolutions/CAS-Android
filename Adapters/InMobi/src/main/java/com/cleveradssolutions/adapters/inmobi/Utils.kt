package com.cleveradssolutions.adapters.inmobi

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.annotation.MainThread
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationAdBid
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus


internal fun AdMetaInfo.tryHandleFetchInMobiAd(
    ad: MediationAd?,
    request: MediationAdUnitRequest?
): Boolean {
    val bidRequest = request as? MediationAdBidRequest ?: return false
    val bidAd = ad as? MediationAdBid ?: return false

    if (BuildConfig.DEBUG) {
        bidRequest.debug("(D) Fetch ad $bidInfo")
    }
    /*
     Example: adInfo.bidInfo
     {"adSourceName":"AerMarket AdSource","buyerName":"InMobi","buyerPrice":2.094564,"bidValue":2.094564}
     */

    bidAd.creativeId = creativeID
    bidAd.costPerMille = bid
    bidAd.revenuePrecision = AdRevenuePrecision.PRECISE
    bidRequest.onSuccess(bidAd)
    return true
}

internal fun AdMetaInfo.fillAdInfo(ad: MediationAd, request: MediationAdUnitRequest) {
    ad.creativeId = creativeID

    val cost = bid
    if (cost > 0.0) {
        ad.costPerMille = cost
        ad.revenuePrecision = if (request.bidResponse != null)
            AdRevenuePrecision.PRECISE
        else
            AdRevenuePrecision.FLOOR
    }
}

internal fun InMobiAdRequestStatus.toCASError(): AdError =
    when (statusCode) {
        InMobiAdRequestStatus.StatusCode.NO_FILL ->
            AdError.NO_FILL

        InMobiAdRequestStatus.StatusCode.NETWORK_UNREACHABLE ->
            AdError.NO_CONNECTION

        InMobiAdRequestStatus.StatusCode.REQUEST_INVALID,
        InMobiAdRequestStatus.StatusCode.MISSING_REQUIRED_DEPENDENCIES,
        InMobiAdRequestStatus.StatusCode.MONETIZATION_DISABLED,
        InMobiAdRequestStatus.StatusCode.CALLED_FROM_WRONG_THREAD,
        InMobiAdRequestStatus.StatusCode.CONFIGURATION_ERROR ->
            AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        // TODO: InMobi can call failed to load when ad is expired
        InMobiAdRequestStatus.StatusCode.AD_NO_LONGER_AVAILABLE ->
            AdError.EXPIRED
        //InMobiAdRequestStatus.StatusCode.GDPR_COMPLIANCE_ENFORCED ->

        else -> AdError(AdErrorCode.INTERNAL_ERROR, message)
    }

private const val EXTRA_KEY_ADAPTIVE_AD_SLOT = "ab-ad-slot"
private const val EXTRA_KEY_ADAPTIVE_TYPE = "ab-type"
private const val EXTRA_INLINE_BANNER_TYPE = "inline"
private const val EXTRA_ANCHORED_BANNER_TYPE = "anchored"

internal fun CASInMobiExtras(request: MediationAdUnitRequest): HashMap<String, String> {
    val extras = HashMap<String, String>()
    // If the COPPA value isn't specified by the publisher,
    // InMobi SDK expects the default value to be `0`.
    extras["coppa"] = if (request.privacy.isAppliesCOPPA() == true) "1" else "0"

    extras["tp"] = request.getStrParameter(Constants.KEY_MEDIATION_NAME)
        ?: "c_casai"

    extras["tp-ver"] = request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER)
        ?: CAS.getSDKVersion()
    return extras
}

private fun HashMap<String, String>.addAdaptiveBannerExtras(
    request: MediationBannerAdRequest
) {
    if (request.adSizeId == AdSizeId.MREC) return

    val adSize = request.adSize
    this[EXTRA_KEY_ADAPTIVE_TYPE] = if (adSize.isAdaptive) EXTRA_ANCHORED_BANNER_TYPE
    else if (adSize.isInline) EXTRA_INLINE_BANNER_TYPE
    else return

    this[EXTRA_KEY_ADAPTIVE_AD_SLOT] = "${adSize.width}x${adSize.height}"
}

internal fun getBannerExtras(request: MediationBannerAdRequest): HashMap<String, String> {
    return CASInMobiExtras(request).apply {
        addAdaptiveBannerExtras(request)
    }
}

internal fun getSignalExtras(request: MediationAdSignalRequest): HashMap<String, String> {
    return if (request.format.isAdView) {
        getBannerExtras(request.forBannerAd())
    } else {
        CASInMobiExtras(request)
    }
}

/**
 * Caused by android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed
 *        at android.webkit.WebViewFactory.getWebViewContextAndSetProvider(WebViewFactory.java:339)
 *        at android.webkit.WebViewFactory.getProviderClass(WebViewFactory.java:402)
 *        at android.webkit.WebViewFactory.getProvider(WebViewFactory.java:252)
 *        at android.webkit.WebView.getFactory(WebView.java:2576)
 *        at android.webkit.WebView.ensureProviderCreated(WebView.java:2570)
 *        at android.webkit.WebView.setOverScrollMode(WebView.java:2657)
 *        at android.view.View.<init>(View.java:5697)
 *        at android.view.View.<init>(View.java:5886)
 *        at android.view.ViewGroup.<init>(ViewGroup.java:715)
 *        at android.widget.AbsoluteLayout.<init>(AbsoluteLayout.java:59)
 *        at android.webkit.WebView.<init>(WebView.java:413)
 *        at android.webkit.WebView.<init>(WebView.java:355)
 *        at android.webkit.WebView.<init>(WebView.java:337)
 *        at android.webkit.WebView.<init>(WebView.java:324)
 *        at android.webkit.WebView.<init>(WebView.java:314)
 *        at com.inmobi.media.ie.b(UnifiedSdk.kt:17)
 *        at com.inmobi.media.-$$Lambda$v4utXOKgKLbmXPxpO-6d3-mWyEg.run(lambda:2)
 *        at android.os.Handler.handleCallback(Handler.java:938)
 *        at android.os.Handler.dispatchMessage(Handler.java:99)
 *        at android.os.Looper.loop(Looper.java:246)
 *        at android.app.ActivityThread.main(ActivityThread.java:8512)
 *        at java.lang.reflect.Method.invoke(Method.java)
 *        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:602)
 *        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1139)
 */
@MainThread
@Throws(Throwable::class)
@SuppressLint("PrivateApi", "WebViewApiAvailability")
internal fun isWebViewInstalled(context: Context): Boolean {
    // There was no WebView Package before Lollipop,
    // the WebView code was part of the framework back then.

//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//        return true
//    }
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WebView.getCurrentWebViewPackage() != null
        }
        val factoryClass = Class.forName("android.webkit.WebViewFactory")
        val loadedWebViewPackageInfo = factoryClass
            .getMethod("getLoadedPackageInfo")
            .invoke(null)
        if (loadedWebViewPackageInfo != null)
            return true

        // If WebViewFactory.getLoadedPackageInfo() returns null then WebView hasn't been loaded
        // yet, in that case we need to fetch the name of the WebView package, and fetch the
        // corresponding PackageInfo through the PackageManager
        val webviewPackageName = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            factoryClass.getMethod("getWebViewPackageName")
                .invoke(null) as? String
        } else {
            Class.forName("android.webkit.WebViewUpdateService")
                .getMethod("getCurrentWebViewPackageName")
                .invoke(null) as? String
        }

        if (webviewPackageName == null) return false
        context.packageManager.getPackageInfo(webviewPackageName, 0)
        true
    } catch (e: Throwable) {
        false
    }
}

