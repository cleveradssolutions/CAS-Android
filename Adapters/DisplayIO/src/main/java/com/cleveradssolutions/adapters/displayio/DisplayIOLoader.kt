package com.cleveradssolutions.adapters.displayio

import com.brandio.ads.ads.Ad
import com.brandio.ads.ads.supers.NativeAdInterface
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.AdRequestListener
import com.brandio.ads.placements.Placement
import com.brandio.ads.request.CompliantState
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.AdRevenuePrecision
import com.cleversolutions.ads.AdError

internal class DisplayIOLoader(
    request: MediationAdUnitRequest,
    private val placement: Placement
) : MediationAdLoader, Placement.OnORTBLoadListener, AdRequestListener {

    private var request: MediationAdUnitRequest? = request

    fun loadWithFloor(request: MediationAdUnitRequest) {
        val builder = placement.newAdRequestBuilder()
            .setUserId(request.targeting.userId)
            .setBidFloor(request.floor)

        request.privacy.isAppliesCOPPA()?.let {
            builder.setChildCompliant(
                if (it) CompliantState.YES else CompliantState.NO
            )
        }
        request.targeting.appStoreUrl?.let {
            builder.setStoreUrl(it)
        }
        request.targeting.appVersion?.let {
            builder.setAppVersion(it)
        }
        val bidRequest = builder.build()
        bidRequest.setAdRequestListener(this)
        bidRequest.requestAd()
    }

    override fun onLoaded(ad: Ad) {
        val request = request ?: return
        this.request = null

        if (request.format === AdFormat.NATIVE) {
            if (ad !is NativeAdInterface) {
                ad.close()

                request.onFailure(
                    AdError(
                        AdErrorCode.CONFIGURATION_ERROR,
                        "Loaded Ad does not conform to DIONativeAdInterface"
                    )
                )
                return
            }

            val nativeAd = DisplayIONativeAd(ad, ad)

            nativeAd.creativeId = ad.creativeId
            nativeAd.costPerMille = ad.ecpm
            nativeAd.revenuePrecision = AdRevenuePrecision.PRECISE

            request.forNativeAd().onSuccess(nativeAd)
            return
        }

        val casAd = DisplayIOAd(placement, ad)

        casAd.creativeId = ad.creativeId
        casAd.costPerMille = ad.ecpm
        casAd.revenuePrecision = AdRevenuePrecision.PRECISE

        if (request.format.isAdView) {
            request.forBannerAd().onSuccess(casAd)
        } else {
            request.forScreenAd().onSuccess(casAd)
        }
    }

    override fun onFailToLoad(error: DIOError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdReceived(ad: Ad) {
        onLoaded(ad)
    }

    override fun onNoAds(error: DIOError) {
        onFailToLoad(error)
    }

    override fun onFailedToLoad(error: DIOError) {
        onFailToLoad(error)
    }
}