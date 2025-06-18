package com.cleveradssolutions.sampleapp.selection

import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.appopen.AppOpenAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.AdaptiveBannerAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.BannerAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.MRECBannerAdActivity
import com.cleveradssolutions.sampleapp.interstitial.InterstitialAdActivity
import com.cleveradssolutions.sampleapp.native.NativeAdActivity
import com.cleveradssolutions.sampleapp.native.NativeTemplateAdActivity
import com.cleveradssolutions.sampleapp.rewarded.RewardedAdActivity
import kotlin.reflect.KClass

enum class AdContainers(val titleResId: Int, val activity: KClass<*>) {
    Banner(R.string.adFormats_bannerAd, BannerAdActivity::class),
    AdaptiveBanner(R.string.adFormats_adaptiveBannerAd, AdaptiveBannerAdActivity::class),
    MrecBanner(R.string.adFormats_mrecBannerAd, MRECBannerAdActivity::class),
    Native(R.string.adFormats_nativeAd, NativeAdActivity::class),
    NativeTemplate(R.string.adFormats_nativeTemplateAd, NativeTemplateAdActivity::class),
    AppOpen(R.string.adFormats_appOpenAd, AppOpenAdActivity::class),
    Interstitial(R.string.adFormats_interstitialAd, InterstitialAdActivity::class),
    Rewarded(R.string.adFormats_rewardedAd, RewardedAdActivity::class);
}
