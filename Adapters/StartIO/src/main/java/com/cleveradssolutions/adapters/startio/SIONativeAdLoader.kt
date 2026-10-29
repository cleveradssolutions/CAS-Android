package com.cleveradssolutions.adapters.startio

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.startapp.sdk.ads.nativead.NativeAdPreferences
import com.startapp.sdk.ads.nativead.StartAppNativeAd
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.adlisteners.AdEventListener

private const val IMAGE_SIZE_72X72 = 0
private const val IMAGE_SIZE_100X100 = 1
private const val IMAGE_SIZE_150X150 = 2
private const val IMAGE_SIZE_340X340 = 3
private const val IMAGE_SIZE_1200X628 = 4

internal class SIONativeAdLoader(request: MediationNativeAdRequest) : MediationAdLoader,
    AdEventListener {
    private var request: MediationNativeAdRequest? = request

    init {
        val prefs = NativeAdPreferences()
        if (request.bidResponse == null && request.floor > 0.0) {
            prefs.minCpm = request.floor
        }

        prefs.adTag = request.unitId
        prefs.placementId = request.unitLabel

        if (request.isTestRequest)
            prefs.isTestMode = true

        if (request.isStartVideoMuted)
            prefs.muteVideo()

        CAS.targetingOptions.keywords?.let {
            prefs.keywords = it.joinToString(",")
        }
        CAS.targetingOptions.location?.let {
            prefs.latitude = it.latitude
            prefs.longitude = it.longitude
        }

        // Defaults
        prefs.adsNumber = 1
        prefs.isAutoBitmapDownload = true
        // prefs.setPrimaryImageSize(IMAGE_SIZE_150X150)
        // prefs.setSecondaryImageSize(IMAGE_SIZE_150X150)

        prefs.primaryImageSize = IMAGE_SIZE_340X340
        request.getParameter("native_media_size")?.let {
            prefs.setPrimaryImageSize(imageSizeToInt(it.toString()))
        }

        prefs.secondaryImageSize = IMAGE_SIZE_150X150
        request.getParameter("native_icon_size")?.let {
            prefs.setSecondaryImageSize(imageSizeToInt(it.toString()))
        }

        val nativeAd = StartAppNativeAd(request.context)
        nativeAd.setPreferences(prefs)
        nativeAd.loadAd(this, request.bidResponse)
    }

    private fun imageSizeToInt(input: String): Int {
        return when (input) {
            "72x72" -> IMAGE_SIZE_72X72
            "100x100" -> IMAGE_SIZE_100X100
            "150x150" -> IMAGE_SIZE_150X150
            "340x340" -> IMAGE_SIZE_340X340
            "1200x628" -> IMAGE_SIZE_1200X628
            else -> IMAGE_SIZE_340X340
        }
    }

    override fun onReceiveAd(ad: Ad) {
        val request = request ?: return
        this.request = null
        val nativeAd = ad as StartAppNativeAd

        nativeAd.nativeAds?.firstOrNull()?.let {
            request.onSuccess(SIONativeAd(it, request.context))
            return
        }
        request.onFailure(AdError.NO_FILL)
    }

    override fun onFailedToReceiveAd(ad: Ad?) {
        disableAutoRetries()
        request?.onFailure(getCASError(ad?.errorMessage, ad?.isBelowMinCPM))
        request = null
    }
}