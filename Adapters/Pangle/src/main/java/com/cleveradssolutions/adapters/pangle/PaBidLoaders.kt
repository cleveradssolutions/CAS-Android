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
import com.cleveradssolutions.mediation.core.MediationAdBidRequest


internal class BannerBidLoader(request: MediationAdBidRequest?) :
    BaseBidLoader<PAGBannerAd, BannerAd>(request), PAGBannerAdLoadListener

internal class AppOpenBidLoader(request: MediationAdBidRequest?) :
    BaseBidLoader<PAGAppOpenAd, AppOpenAd>(request), PAGAppOpenAdLoadListener

internal class InterstitialBidLoader(request: MediationAdBidRequest?) :
    BaseBidLoader<PAGInterstitialAd, InterstitialAd>(request), PAGInterstitialAdLoadListener

internal class RewardedBidLoader(request: MediationAdBidRequest?) :
    BaseBidLoader<PAGRewardedAd, RewardedAd>(request), PAGRewardedAdLoadListener

internal class NativeBidLoader(request: MediationAdBidRequest?) :
    BaseBidLoader<PAGNativeAd, NativeAdContent>(request), PAGNativeAdLoadListener