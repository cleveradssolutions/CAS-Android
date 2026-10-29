package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.yango.BuildConfig
import com.cleveradssolutions.adapters.yango.YaAppOpenLoader
import com.cleveradssolutions.adapters.yango.YaBannerAd
import com.cleveradssolutions.adapters.yango.YaInterstitialLoader
import com.cleveradssolutions.adapters.yango.YaNativeAdLoader
import com.cleveradssolutions.adapters.yango.YaRewardedLoader
import com.cleveradssolutions.adapters.yango.YaSignalCollector
import com.cleveradssolutions.adapters.yango.yangoTheme
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationAppOpenAdRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.android.CAS
import com.yandex.mobile.ads.common.AdActivity
import com.yandex.mobile.ads.common.AdTheme
import com.yandex.mobile.ads.common.AdapterIdentity
import com.yandex.mobile.ads.common.YandexAds

class YandexAdapter : MediationAdapterBase() {
    private var loaderAppOpen = YaAppOpenLoader()
    private var loaderInterstitial = YaInterstitialLoader()
    private var loaderRewarded = YaRewardedLoader()
    private var loaderNative = YaNativeAdLoader()

    override fun getSDKVersion(): String = YandexAds.libraryVersion
    override fun getMinSDKVersion(): String? = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = AdActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.allFormats or
            AdapterSupport.iron or
            AdapterSupport.cas or
            AdapterSupport.max


    override fun initAds(request: MediationInitAdRequest) {
        YandexAds.enableLogging(request.isDebugLogsEnabled)
        onUserPrivacyChanged(request.privacy)

        YandexAds.enableDebugErrorIndicator(request.isTestRequest)

        YandexAds.setAdapterIdentity(
            AdapterIdentity(
                adapterNetworkName = request.getStrParameter(Constants.KEY_MEDIATION_NAME)
                    ?: "cas",
                adapterVersion = request.getStrParameter(Constants.KEY_MEDIATION_ADAPTER_VER)
                    ?: getAdapterVersion(),
                adapterNetworkVersion = request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER)
                    ?: CAS.getSDKVersion()
            )
        )

        YandexAds.initialize(request.context) {
            initRequest.onSuccess()
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        YaSignalCollector(request).collect()
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN)
            return super.loadAd(request)
        return YaBannerAd()
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader =
        if (loaderInterstitial.isLoading())
            YaInterstitialLoader()
        else
            loaderInterstitial

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader =
        if (loaderRewarded.isLoading())
            YaRewardedLoader()
        else
            loaderRewarded

    override fun loadAd(request: MediationAppOpenAdRequest): MediationAdLoader =
        if (loaderAppOpen.isLoading())
            YaAppOpenLoader()
        else
            loaderAppOpen

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader =
        if (loaderNative.isLoading())
            YaNativeAdLoader()
        else
            loaderNative

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        // Google rejects apps that don't ask for location permission but use the location.
        // False by default. So allow set consent to user
        YandexAds.setLocationTracking(CAS.targetingOptions.locationCollectionEnabled)

        privacy.hasConsent(AdSourceId.YANGO_ADS)?.let {
            YandexAds.setUserConsent(it)
        }

        privacy.isAppliesCOPPA()?.let {
            YandexAds.setAgeRestricted(it)
        }

        yangoTheme = getMetaData("dark_mode")?.let {
            if (it == "1") AdTheme.DARK else AdTheme.LIGHT
        }
    }
}