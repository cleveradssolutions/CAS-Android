package com.cleveradssolutions.adapters.inmobi

import android.view.ViewGroup
import com.cleveradssolutions.mediation.core.MediationAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.debugCatch
import com.inmobi.ads.AdMetaInfo
import com.inmobi.ads.InMobiAdRequestStatus
import com.inmobi.ads.InMobiBanner
import com.inmobi.ads.listeners.BannerAdEventListener

internal class IMBannerAdListener : BannerAdEventListener() {
    var request: MediationBannerAdRequest? = null
    var ad: MediationAd? = null

    override fun onAdFetchSuccessful(banner: InMobiBanner, info: AdMetaInfo) {
        if (info.tryHandleFetchInMobiAd(ad, request)) {
            request = null
            ad = null
        }
    }

    override fun onAdLoadSucceeded(banner: InMobiBanner, info: AdMetaInfo) {
        val request = request ?: return
        this.request = null
        // InMobi banner loaded in container, so we must unwrap it
        request.debugCatch("Unwrap banner") {
            val container = banner.parent as? ViewGroup
            container?.removeView(banner)
        }
        val ad = IMBannerAd(banner)
        info.fillAdInfo(ad, request)
        this.ad = ad
        request.onSuccess(ad)
    }

    override fun onAdFetchFailed(banner: InMobiBanner, status: InMobiAdRequestStatus) {
        request?.onFailure(status.toCASError())
        request = null
        ad = null
    }

    override fun onAdLoadFailed(banner: InMobiBanner, status: InMobiAdRequestStatus) {
        onAdFetchFailed(banner, status)
    }

    override fun onAdImpression(banner: InMobiBanner) {
        ad?.let {
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdClicked(banner: InMobiBanner, p1: MutableMap<Any, Any>) {
        ad?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdDisplayed(banner: InMobiBanner) {
        ad?.let {
            it.listener?.onAdShowed(it)
        }
    }

    override fun onAdDismissed(banner: InMobiBanner) {
        ad?.let {
            it.listener?.onAdDismissed(it)
        }
    }
}