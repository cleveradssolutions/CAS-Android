package com.cleveradssolutions.adapters.verve

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.NativeAd
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest

internal class VerveNativeLoader(request: MediationNativeAdRequest) : MediationAdLoader,
    HyBidNativeAdRequest.RequestListener {

    private var request: MediationNativeAdRequest? = request
    private val nativeRequest = HyBidNativeAdRequest()

    init {
        nativeRequest.setMediation(true)
        nativeRequest.setPreLoadMediaAssets(true)
        val bidResponse = request.bidResponse

        if (!bidResponse.isNullOrEmpty()) {
            nativeRequest.prepareAd(bidResponse, this)
        } else {
            nativeRequest.load(HyBid.getAppToken(), request.unitId, this)
        }
    }

    override fun onRequestSuccess(ad: NativeAd) {
        val request = request ?: return
        this.request = null

        try {
            request.onSuccess(VerveNativeAd(ad, request.context))
        } catch (error: Exception) {
            request.onFailure(error.toVerveAdError())
        }
    }

    override fun onRequestFail(error: Throwable?) {
        val currentRequest = request ?: return
        request = null
        currentRequest.onFailure(error.toVerveAdError())
    }
}
