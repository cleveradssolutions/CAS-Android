package com.cleveradssolutions.adapters

import com.cleveradssolutions.adapters.dtexchange.DTExBannerAd
import com.cleveradssolutions.adapters.dtexchange.DTExNativeAdLoader
import com.cleveradssolutions.adapters.dtexchange.DTExScreenAd
import com.cleveradssolutions.adapters.fyber.BuildConfig
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.MediationUserPrivacy
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdSourceId
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS
import com.fyber.inneractive.sdk.activities.InneractiveFullscreenAdActivity
import com.fyber.inneractive.sdk.external.BidTokenProvider
import com.fyber.inneractive.sdk.external.InneractiveAdManager
import com.fyber.inneractive.sdk.external.InneractiveUserConfig
import com.fyber.inneractive.sdk.external.OnFyberMarketplaceInitializedListener
import com.fyber.inneractive.sdk.external.OnFyberMarketplaceInitializedListener.FyberInitStatus

open class DTExchangeAdapter : MediationAdapterBase(),
    OnFyberMarketplaceInitializedListener {
    override fun getSDKVersion(): String = InneractiveAdManager.getVersion()
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = InneractiveFullscreenAdActivity::class.java
    override fun supportBidding(): Int = AdapterSupport.banner or
            AdapterSupport.mrec or
            AdapterSupport.inter or
            AdapterSupport.reward or
            AdapterSupport.nativ or
            AdapterSupport.iron or
            AdapterSupport.max or
            AdapterSupport.admob

    override val isInitialized: Boolean
        get() = InneractiveAdManager.wasInitialized()

    override fun initAds(request: MediationInitAdRequest) {
        val appID = request.requireAppID() ?: return

        // 2 - verbose, 3 - debug, 4 - warning, 6 - error
        if (request.isDebugLogsEnabled)
            InneractiveAdManager.setLogLevel(2)
        else
            InneractiveAdManager.setLogLevel(6)

        /**
         * NOTE: Digital Turbine requires that we set this API once per app session,
         * before the SDK is initialized.
         *
         * Documentation:
         * - Calling the COPPA API must be performed after a successful initialization of the DT SDK
         * - You must pass the COPPA state after every successful init of the SDK
         */
        if (request.privacy.isAppliesCOPPA() == true)
            InneractiveAdManager.currentAudienceIsAChild()

        InneractiveAdManager.setMediationName(
            request.getStrParameter(Constants.KEY_MEDIATION_NAME) ?: "CAS"
        )
        InneractiveAdManager.setMediationVersion(
            request.getStrParameter(Constants.KEY_MEDIATION_SDK_VER) ?: CAS.getSDKVersion()
        )

        val targetOptions = CAS.targetingOptions
        val userConfig = InneractiveUserConfig()
        if (targetOptions.gender != TargetingOptions.GENDER_UNKNOWN) {
            userConfig.gender = if (targetOptions.gender == TargetingOptions.GENDER_MALE)
                InneractiveUserConfig.Gender.MALE
            else
                InneractiveUserConfig.Gender.FEMALE
        }
        InneractiveAdManager.setUserParams(userConfig)

        InneractiveAdManager.initialize(request.context, appID, this)
    }

    override fun onFyberMarketplaceInitialized(status: FyberInitStatus?) {
        when (status) {
            FyberInitStatus.SUCCESSFULLY -> {
                onUserPrivacyChanged(initRequest.privacy)
                initRequest.onSuccess()
            }

            FyberInitStatus.INVALID_APP_ID ->
                initRequest.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "Invalid AppId"))

            FyberInitStatus.FAILED_NO_KITS_DETECTED ->
                initRequest.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, "No kits detected"))

            else -> {
                initRequest.onFailure(AdError.NOT_INITIALIZED)
            }
        }
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val signal = BidTokenProvider.getBidderToken()
        request.onSuccess(signal.orEmpty())
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        return DTExNativeAdLoader(request)
    }

    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if (request.adSizeId == AdSizeId.THIN) {
            return super.loadAd(request)
        }
        return DTExBannerAd(request)
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? {
        return DTExScreenAd(request)
    }

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? {
        return DTExScreenAd(request)
    }

    override fun onUserPrivacyChanged(privacy: MediationUserPrivacy) {
        // After Initialize completed only
        if (!InneractiveAdManager.wasInitialized())
            return

        CAS.targetingOptions.userID?.let {
            InneractiveAdManager.setUserId(it)
        }

        if (privacy.isAppliesGDPR()) {
            privacy.hasConsent(AdSourceId.DT_EXCHANGE)?.let {
                /**
                 * When you set a GDPR consent value for a user, DT concludes that you
                 * have decided that the user is subject to GDPR rules, even if the
                 * user is outside Europe.
                 * If, based on your legal position, your non-EU users are not subject
                 * to GDPR, do not pass any consent value for them.
                 */
                InneractiveAdManager.setGdprConsent(it)
            }
        }

        privacy.getConsentString()?.let {
            InneractiveAdManager.setGdprConsentString(it)
        }

        InneractiveAdManager.setUSPrivacyString(
            privacy.getUSPrivacyString()
        )

        if (privacy.isAppliesCOPPA() == true)
            InneractiveAdManager.currentAudienceAppliesToCoppa()
    }

    override fun onMuteAdSoundsChanged(muted: Boolean) {
        InneractiveAdManager.setMuteVideo(muted)
    }
}