package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAd
import com.bytedance.sdk.openadsdk.api.banner.PAGBannerAdLoadListener
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadListener
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdLoadListener
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAd
import com.bytedance.sdk.openadsdk.api.open.PAGAppOpenAdLoadListener
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAd
import com.bytedance.sdk.openadsdk.api.reward.PAGRewardedAdLoadListener
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError


internal class BannerLoadListener(
    private var request: MediationBannerAdRequest?
) : PAGBannerAdLoadListener {
    override fun onAdLoaded(ad: PAGBannerAd?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }
        request.debugExtraInfo(ad)
        request.onSuccess(BannerAd(ad))
    }

    override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }
}

internal class InterstitialLoadListener(
    private var request: MediationScreenAdRequest?
) : PAGInterstitialAdLoadListener {
    override fun onAdLoaded(ad: PAGInterstitialAd?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }
        request.debugExtraInfo(ad)
        request.onSuccess(InterstitialAd(ad))
    }

    override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }
}

internal class AppOpenLoadListener(
    private var request: MediationScreenAdRequest?
) : PAGAppOpenAdLoadListener {
    override fun onAdLoaded(ad: PAGAppOpenAd?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }
        request.debugExtraInfo(ad)
        request.onSuccess(AppOpenAd(ad))
    }

    override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }
}

internal class RewardedLoadListener(
    private var request: MediationScreenAdRequest?
) : PAGRewardedAdLoadListener {
    override fun onAdLoaded(ad: PAGRewardedAd?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }
        request.debugExtraInfo(ad)
        request.onSuccess(RewardedAd(ad))
    }

    override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }
}

internal class NativeLoadListener(
    private var request: MediationNativeAdRequest?
) : PAGNativeAdLoadListener {
    override fun onAdLoaded(ad: PAGNativeAd?) {
        val request = request ?: return
        this.request = null
        if (ad == null) {
            request.onFailure(AdError.NO_FILL)
            return
        }
        request.debugExtraInfo(ad)
        request.onSuccess(NativeAdContent(ad))
    }

    override fun onError(code: Int, message: String?) {
        request.onPaFailure(code, message)
        request = null
    }
}