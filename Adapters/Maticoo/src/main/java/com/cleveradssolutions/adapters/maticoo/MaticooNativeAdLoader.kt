package com.cleveradssolutions.adapters.maticoo

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.zmaticoo.sdk.ads.nativead.NativeAd
import com.zmaticoo.sdk.ads.nativead.NativeAdListener
import com.zmaticoo.sdk.ads.nativead.NativeAdLoader
import com.zmaticoo.sdk.ads.nativead.NativeAdOptions
import com.zmaticoo.sdk.ads.nativead.VideoOptions
import com.zmaticoo.sdk.base.common.MaticooIds
import com.zmaticoo.sdk.flow.model.ComponentError

internal class MaticooNativeAdLoader(
    request: MediationNativeAdRequest,
    localExtras: Map<String, Any>
) : NativeAdListener(), MediationAdLoader {

    private var request: MediationNativeAdRequest? = request
    private var loadedAd: MaticooNativeAd? = null
    private val loader: NativeAdLoader

    init {
        val videoOptions = VideoOptions.Builder()
            //.setVideoAdListener(getVideoAdListener())
            .setStartMuted(request.isStartVideoMuted) // default false
            .build()
        val nativeAdOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .setExtras(localExtras)
            .build()

        loader = NativeAdLoader.Builder(request.unitId)
            .withNativeAdOptions(nativeAdOptions)
            .withAdListener(this)
            .build()
        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            loader.loadAd(bidResponse)
        } else {
            loader.loadAd()
        }
    }

    override fun onAdLoaded(ids: MaticooIds, nativeAd: NativeAd) {
        request?.onSuccess(MaticooNativeAd(nativeAd).also {
            loadedAd = it
        })
        request = null
    }

    override fun onAdFailedToLoad(p0: MaticooIds?, error: ComponentError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdDisplayFailed(p0: MaticooIds?, error: ComponentError) {
        loadedAd?.let {
            it.listener?.onAdFailedToShow(it, error.toCASError())
        }
    }

    override fun onAdClicked(p0: MaticooIds?) {
        loadedAd?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdDisplayed(p0: MaticooIds?) {
        loadedAd?.let {
            it.listener?.onAdImpression(it)
        }
    }
}