package com.cleveradssolutions.adapters.inmobi

import androidx.annotation.MainThread
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus
import com.inmobi.ads.InMobiNative
import com.inmobi.ads.listeners.NativeAdEventListener


internal class IMNativeAdLoader @MainThread constructor(
    request: MediationNativeAdRequest,
    placementId: Long
) : NativeAdEventListener(), MediationAdLoader {
    private var request: MediationNativeAdRequest? = request
    private val impl = InMobiNative(request.context, placementId, this)
    private var instance: IMNativeAd? = null

    init {
        impl.setExtras(CASInMobiExtras(request))

        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            impl.load(bidResponse.toByteArray())
        } else {
            impl.load()
        }
    }

    override fun onAdLoadSucceeded(ad: InMobiNative, info: AdMetaInfo) {
        val request = request ?: return
        this.request = null

        val newAd = IMNativeAd(ad)
        info.fillAdInfo(newAd, request)
        instance = newAd
        request.onSuccess(newAd)
    }

    override fun onAdLoadFailed(ad: InMobiNative, requestStatus: InMobiAdRequestStatus) {
        request?.onFailure(requestStatus.toCASError())
        request = null
    }

    override fun onAdImpression(ad: InMobiNative) {
        instance?.let {
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdClicked(ad: InMobiNative) {
        instance?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdFullScreenDisplayed(ad: InMobiNative) {
        instance?.let {
            it.listener?.onAdShowed(it)
        }
    }

    override fun onAdFullScreenDismissed(ad: InMobiNative) {
        instance?.let {
            it.listener?.onAdDismissed(it)
        }
    }

}