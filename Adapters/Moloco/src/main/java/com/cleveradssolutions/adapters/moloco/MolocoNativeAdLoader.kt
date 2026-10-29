package com.cleveradssolutions.adapters.moloco

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.AdLoad
import com.moloco.sdk.publisher.CreateNativeAdCallback
import com.moloco.sdk.publisher.MolocoAd
import com.moloco.sdk.publisher.MolocoAdError
import com.moloco.sdk.publisher.NativeAd

internal class MolocoNativeAdLoader(
    request: MediationNativeAdRequest,
    private val bidResponse: String
) : MediationAdLoader, AdLoad.Listener, CreateNativeAdCallback {

    private var request: MediationNativeAdRequest? = request
    private var nativeAd: NativeAd? = null

    override fun invoke(createdAd: NativeAd?, error: MolocoAdError.AdCreateError?) {
        if (createdAd == null) {
            this.request?.onFailure(error.toCASError())
            this.request = null
        } else {
            nativeAd = createdAd
            createdAd.load(bidResponse, this)
        }
    }

    override fun onAdLoadSuccess(molocoAd: MolocoAd) {
        val request = request ?: return
        this.request = null
        val ad = nativeAd
        if (ad == null) {
            request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, "Moloco native ad is null"))
            return
        }

        val assets = ad.assets
        if (assets == null) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Moloco native assets are null"
                )
            )
            return
        }

        val content = MolocoNativeAd(ad, assets)
        content.creativeId = molocoAd.creativeId
        request.onSuccess(content)
    }

    override fun onAdLoadFailed(molocoAdError: MolocoAdError) {
        request?.onFailure(molocoAdError.toCASError())
        request = null
    }
}

