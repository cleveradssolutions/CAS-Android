package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.unity.BuildConfig
import com.cleveradssolutions.adapters.unity.UnityBannerAd
import com.cleveradssolutions.adapters.unity.UnityScreenAdLoader
import com.cleveradssolutions.adapters.unity.UnitySignalListener
import com.cleveradssolutions.adapters.unity.mapUnityError
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.unity3d.ads.BannerAd
import com.unity3d.ads.BannerConfiguration
import com.unity3d.ads.BannerSize
import com.unity3d.ads.InitializationConfiguration
import com.unity3d.ads.InitializationListener
import com.unity3d.ads.InterstitialAd
import com.unity3d.ads.LoadConfiguration
import com.unity3d.ads.LogLevel
import com.unity3d.ads.MediationInfo
import com.unity3d.ads.RewardedAd
import com.unity3d.ads.TokenConfiguration
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsError
import com.unity3d.ads.AdFormat as UnityFormat

class UnityAdapter : MediationAdapterBase(), InitializationListener {

    private var mediationInfo = MediationInfo(
        "CAS", CAS.getSDKVersion(), BuildConfig.VERSION
    )

    override fun getSDKVersion(): String = UnityAds.version
    override fun getMinSDKVersion(): String = com.unity3d.ads.BuildConfig.VERSION_NAME
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = InterstitialAd::class.java

    override fun supportBidding(): Int = AdapterSupport.banner or
            AdapterSupport.mrec or
            AdapterSupport.inter or
            AdapterSupport.reward or
            AdapterSupport.admob or
            AdapterSupport.iron or
            AdapterSupport.max

    override val isInitialized: Boolean
        get() = UnityAds.isInitialized

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        request.getStrParameter(Constants.KEY_MEDIATION_NAME)?.let {
            mediationInfo = MediationInfo(
                it,
                request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER) ?: CAS.getSDKVersion(),
                request.getStrParameter(Constants.KEY_MEDIATION_ADAPTER_VER) ?: getAdapterVersion()
            )
        }

        onUserPrivacyChanged(request.privacy)

        val configBuilder = InitializationConfiguration.Builder(appID)
            .withTestMode(request.isTestRequest)
            .withMediationInfo(mediationInfo)
            .withLogLevel(if (request.isDebugLogsEnabled) LogLevel.DEBUG else LogLevel.ERROR)

        UnityAds.initialize(configBuilder.build(), this)
    }

    override fun onInitializationComplete(error: UnityAdsError?) {
        if (error == null) {
            initRequest.onSuccess()
        } else {
            initRequest.onFailure(mapUnityError(error))
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val unityFormat = if (request.format.isAdView)
            UnityFormat.BANNER
        else when (request.format.value) {
            AdFormat.INTERSTITIAL.value -> UnityFormat.INTERSTITIAL
            AdFormat.REWARDED.value -> UnityFormat.REWARDED
            else -> {
                request.onFailure(AdError("Not supported format"))
                return
            }
        }

        val builder = TokenConfiguration.Builder(unityFormat)
            .withMediationInfo(mediationInfo)
            .withPlacementId(request.unitId)
        if (unityFormat === UnityFormat.BANNER) {
            getBannerSize(request.forBannerAd())?.let {
                builder.withBannerSize(it)
            }
        }

        val listener = UnitySignalListener(request)
        UnityAds.getToken(builder.build(), listener)
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        val bannerSize = getBannerSize(request) ?: return super.loadAd(request)

        val bannerAd = UnityBannerAd(request)
        val builder = BannerConfiguration.Builder(
            request.unitId,
            bannerSize,
            bannerAd
        )
        builder.withMediationInfo(mediationInfo)
        request.bidResponse?.let {
            builder.withAdMarkup(it)
        }

        BannerAd.load(builder.build(), bannerAd)
        return bannerAd
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader {
        val loader = UnityScreenAdLoader<InterstitialAd>(request)
        InterstitialAd.load(buildLoadConfig(request), loader)
        return loader
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader {
        val loader = UnityScreenAdLoader<RewardedAd>(request)
        RewardedAd.load(buildLoadConfig(request), loader)
        return loader
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        privacy.hasConsent(AdSourceId.UNITY_ADS)?.let {
            UnityAds.userConsent = it
        }

        privacy.isOptOutSaleUS()?.let {
            UnityAds.userOptOut = it
        }

        privacy.isAppliesCOPPA()?.let {
            UnityAds.nonBehavioral = it
        }

        UnityAds.userIdentifier = CAS.targetingOptions.userID
    }

    private fun buildLoadConfig(request: MediationAdUnitRequest): LoadConfiguration {
        val builder = LoadConfiguration.Builder(request.unitId)
        builder.withMediationInfo(mediationInfo)
        request.bidResponse?.let {
            builder.withAdMarkup(it)
        }
        return builder.build()
    }

    private fun getBannerSize(request: MediationBannerAdRequest): BannerSize? {
        if (request.adSizeId == AdSizeId.THIN) return null
        val size = request.adSize
        val isAdaptive = size.isAdaptive || (size.isInline && request.adSizeId != AdSizeId.MREC)
        if (isAdaptive) {
            return BannerSize(size.width, size.height)
        }
        return size.findClosestSize()?.let {
            BannerSize(size.width, size.height)
        }
    }
}