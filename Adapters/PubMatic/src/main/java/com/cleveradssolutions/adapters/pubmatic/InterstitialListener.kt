package com.cleveradssolutions.adapters.pubmatic

import com.cleveradssolutions.mediation.core.MediationAd
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.openwrap.interstitial.POBInterstitial

internal class InterstitialListener(
    private var loader: PubMaticBidLoader?
) : POBInterstitial.POBInterstitialListener() {
    private var loadedAd: MediationAd? = null

    override fun onAdReceived(ad: POBInterstitial) {
        val loader = loader ?: return
        this.loader = null
        val request = loader.request?.forScreenAd() ?: return

        val casAd = PubMaticAd(ad)
        loader.fillAdContent(casAd)
        loadedAd = casAd
        request.onSuccess(casAd)
    }

    override fun onAdFailedToLoad(ad: POBInterstitial, error: POBError) {
        loader?.onBidFailed(ad, error)
        loader = null
    }

    override fun onAdExpired(ad: POBInterstitial) {
        loadedAd?.let {
            it.listener?.onAdExpired(it)
        }
    }

    override fun onAdFailedToShow(ad: POBInterstitial, error: POBError) {
        loadedAd?.let {
            it.listener?.onAdFailedToShow(it, error.toCASError())
        }
    }

    override fun onAdOpened(ad: POBInterstitial) {
        loadedAd?.let {
            //it.listener?.onAdShowed(it)
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdImpression(ad: POBInterstitial) {
        // PubMatic bug when onAdImpression() called immediately after onAdReceived()
        // For Interstitial only
        // Temporary we use onAdOpened() to collect impressions
//        loadedAd?.let {
//            it.listener?.onAdImpression(it)
//        }
    }

    override fun onAdClicked(ad: POBInterstitial) {
        loadedAd?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdClosed(ad: POBInterstitial) {
        loadedAd?.let {
            it.listener?.onAdDismissed(it)
        }
    }
}