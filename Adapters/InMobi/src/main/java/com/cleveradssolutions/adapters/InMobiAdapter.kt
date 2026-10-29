package com.cleveradssolutions.adapters

import androidx.annotation.MainThread
import com.cleveradssolutions.adapters.inmobi.BuildConfig
import com.cleveradssolutions.adapters.inmobi.IMBannerAdLoader
import com.cleveradssolutions.adapters.inmobi.IMNativeAdLoader
import com.cleveradssolutions.adapters.inmobi.IMScreenAdLoader
import com.cleveradssolutions.adapters.inmobi.getSignalExtras
import com.cleveradssolutions.adapters.inmobi.isWebViewInstalled
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdBidRequest
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationRtbAdapter
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import com.inmobi.ads.exceptions.SdkNotInitializedException
import com.inmobi.ads.rendering.InMobiAdActivity
import com.inmobi.compliance.InMobiPrivacyCompliance
import com.inmobi.sdk.InMobiSdk
import com.inmobi.sdk.SdkInitializationListener
import org.json.JSONObject

class InMobiAdapter : MediationRtbAdapter(), SdkInitializationListener {
    override fun getSDKVersion(): String = InMobiSdk.getVersion()
    override fun getMinSDKVersion(): String = InMobiSdk.getVersion()
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = InMobiAdActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.simple or
            AdapterSupport.nativ or
            AdapterSupport.max or
            AdapterSupport.iron or
            AdapterSupport.admob

    override val isInitialized: Boolean
        get() = InMobiSdk.isSDKInitialized()

    // Required Main Thread
    @MainThread
    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        val context = request.context
        if (!isWebViewInstalled(context)) {
            request.onFailure(AdError(AdErrorCode.REJECTED, "Required WebView not installed"))
            return
        }

        InMobiSdk.setLogLevel(
            if (request.isDebugLogsEnabled) InMobiSdk.LogLevel.DEBUG
            else InMobiSdk.LogLevel.NONE
        )

        onMuteAdSoundsChanged(CAS.settings.mutedAdSounds)

        InMobiSdk.init(
            context,
            appID,
            createNewConsent(request.privacy),
            this
        )

        try {
            val targeting = CAS.targetingOptions
            if (targeting.age != 0) {
                InMobiSdk.setAge(targeting.age)
            }
            if (targeting.gender == TargetingOptions.GENDER_MALE)
                InMobiSdk.setGender(InMobiSdk.Gender.MALE)
            else if (targeting.gender == TargetingOptions.GENDER_FEMALE)
                InMobiSdk.setGender(InMobiSdk.Gender.FEMALE)
            targeting.location?.let {
                InMobiSdk.setLocation(it)
            }
        } catch (_: Throwable) {
        }
    }

    override fun onInitializationComplete(error: Error?) {
        if (error == null) {
            onUserPrivacyChanged(initRequest.privacy)
            initRequest.onSuccess()
        } else {
            initRequest.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, error.message))
        }
    }

    @MainThread
    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? =
        if (request.adSizeId == AdSizeId.THIN)
            super.loadAd(request)
        else
            requirePlacementId(request)?.let {
                IMBannerAdLoader(request, it)
            }

    @MainThread
    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? =
        requirePlacementId(request)?.let {
            IMScreenAdLoader(request, it)
        }

    @MainThread
    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? =
        requirePlacementId(request)?.let {
            IMScreenAdLoader(request, it)
        }

    @MainThread
    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? =
        requirePlacementId(request)?.let {
            IMNativeAdLoader(request, it)
        }

    @MainThread
    override fun collectSignals(request: MediationAdSignalRequest) {
        if (!isInitialized) {
            request.onFailure(AdError.NOT_INITIALIZED)
            return
        }
        // Not required value and can be null
        val signal = InMobiSdk.getToken(getSignalExtras(request), null).orEmpty()
        request.onSuccess(signal)
    }

    @MainThread
    override fun fetchAdBid(request: MediationAdBidRequest): MediationAdLoader? {
        val placementId = requirePlacementId(request) ?: return null
        if (request.format.isAdView) {
            val bannerRequest = request.forBannerAd()
            if (bannerRequest.adSizeId == AdSizeId.THIN) {
                return super.fetchAdBid(request)
            }
            return IMBannerAdLoader(bannerRequest, placementId)
        } else if (request.format === AdFormat.INTERSTITIAL
            || request.format === AdFormat.REWARDED
        ) {
            return IMScreenAdLoader(request.forScreenAd(), placementId)
        }
        return super.fetchAdBid(request)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        if (privacy.isAppliesUSPrivacy()) {
            InMobiPrivacyCompliance.setUSPrivacyString(privacy.getUSPrivacyString())
        }

        if (InMobiSdk.isSDKInitialized()) {
            // If the COPPA value isn't specified by the publisher,
            // InMobi SDK expects the default value to be `false`.
            try {
                InMobiSdk.setIsAgeRestricted(privacy.isAppliesCOPPA() == true)
            } catch (e: SdkNotInitializedException) {
                // ignore Not Initialized exception from InMobi SDK
            }

            InMobiSdk.setPartnerGDPRConsent(createNewConsent(privacy))
        }
    }

    override fun onMuteAdSoundsChanged(muted: Boolean) {
        InMobiSdk.setApplicationMuted(muted)
    }

    private fun requirePlacementId(request: MediationAdUnitRequest): Long? {
        val placementId = request.unitId.toLongOrNull()
        if (placementId == null) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "PlacementID is not Integer"
                )
            )
            return null
        }
        return placementId
    }

    private fun createNewConsent(privacy: MediationUserPrivacy): JSONObject =
        JSONObject().apply {
            privacy.hasConsent(AdSourceId.INMOBI)?.let {
                put("partner_gdpr_consent_available", it)
            }
        }
}