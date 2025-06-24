package com.cleveradssolutions.sampleapp.support

import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.appopen.AppOpenAdActivity
import com.cleveradssolutions.sampleapp.appopen.SplashAppOpenAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.AdaptiveBannerAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.BannerAdActivity
import com.cleveradssolutions.sampleapp.banner.xml.MRECBannerAdActivity
import com.cleveradssolutions.sampleapp.interstitial.InterstitialAdActivity
import com.cleveradssolutions.sampleapp.nativead.MultipleNativeAdActivity
import com.cleveradssolutions.sampleapp.nativead.NativeAdActivity
import com.cleveradssolutions.sampleapp.nativead.NativeAdTemplateActivity
import com.cleveradssolutions.sampleapp.rewarded.RewardedAdActivity
import kotlin.reflect.KClass

enum class AdContainers(val titleResId: Int, val activity: KClass<*>) {
    Banner(R.string.adFormats_bannerAd, BannerAdActivity::class),
    AdaptiveBanner(R.string.adFormats_adaptiveBannerAd, AdaptiveBannerAdActivity::class),
    MrecBanner(R.string.adFormats_mrecBannerAd, MRECBannerAdActivity::class),
    Native(R.string.adFormats_nativeAd, NativeAdActivity::class),
    NativeTemplate(R.string.adFormats_nativeAdTemplate, NativeAdTemplateActivity::class),
    MultipleNativeTemplate(R.string.adFormats_multipleNativeAd, MultipleNativeAdActivity::class),
    AppOpen(R.string.adFormats_appOpenAd, AppOpenAdActivity::class),
    SplashAppOpen(R.string.adFormats_splashAppOpenAd, SplashAppOpenAdActivity::class),
    Interstitial(R.string.adFormats_interstitialAd, InterstitialAdActivity::class),
    Rewarded(R.string.adFormats_rewardedAd, RewardedAdActivity::class)
}
