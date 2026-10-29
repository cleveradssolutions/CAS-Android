package com.cleveradssolutions.adapters.chartboost

import android.util.Log
import android.view.View
import com.chartboost.sdk.Mediation
import com.chartboost.sdk.ads.Ad
import com.chartboost.sdk.ads.Banner
import com.chartboost.sdk.ads.Interstitial
import com.chartboost.sdk.ads.Rewarded
import com.chartboost.sdk.callbacks.BannerCallback
import com.chartboost.sdk.callbacks.InterstitialCallback
import com.chartboost.sdk.callbacks.RewardedCallback
import com.chartboost.sdk.events.CacheError
import com.chartboost.sdk.events.CacheEvent
import com.chartboost.sdk.events.ClickError
import com.chartboost.sdk.events.ClickEvent
import com.chartboost.sdk.events.DismissEvent
import com.chartboost.sdk.events.ExpirationEvent
import com.chartboost.sdk.events.ImpressionEvent
import com.chartboost.sdk.events.RewardEvent
import com.chartboost.sdk.events.ShowError
import com.chartboost.sdk.events.ShowEvent
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.debug
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError

internal class ChartboostAd(
    request: MediationAdUnitRequest,
    mediation: Mediation
) : MediationAdBase(), MediationBannerAd, MediationScreenAd, MediationAdLoader,
    BannerCallback, InterstitialCallback, RewardedCallback {
    private var request: MediationAdUnitRequest? = request
    private var needClear = false

    private val ad: Ad = if (request.format.isAdView) {
        val size = when (request.forBannerAd().adSizeId) {
            AdSizeId.LARGE -> Banner.BannerSize.LEADERBOARD
            AdSizeId.MREC -> Banner.BannerSize.MEDIUM
            else -> Banner.BannerSize.STANDARD
        }
        Banner(
            request.context,
            request.unitId,
            size,
            this,
            mediation
        )
    } else if (request.format === AdFormat.REWARDED) {
        Rewarded(request.unitId, this, mediation)
    } else {
        Interstitial(request.unitId, this, mediation)
    }

    init {
        if (request.bidResponse != null) {
            ad.cache(request.bidResponse)
        } else {
            ad.cache()
        }
    }

    override fun showScreen(listener: MediationAdListener) {
        // Chartboost not recommended check isCached() before show
        ad.show()
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        needClear = true
        // Showing a detached banner is not recommended, please add it to the view hierarchy first
        CASHandler.main(500) {
            ad.show()
        }
        return ad as View
    }

    override fun destroy() {
        super.destroy()

        // Require Main thread
        // A WebView method was called on thread 'CASHandler-12'. All WebView methods must be called on the same thread. (Expected Looper Looper (main, tid 2) {dc17781} called on null, FYI main Looper is Looper (main, tid 2) {dc17781})
        CASHandler.main {
            if (ad is Banner)
                ad.detach()
            if (needClear)
                ad.clearCache()
        }
    }

    override fun onAdLoaded(event: CacheEvent, error: CacheError?) {
        onAdLoaded(event.adID, error)
    }

    private fun onAdLoaded(creativeId: String?, error: CacheError?) {
        val request = request ?: return
        this.request = null

        if (error == null) {
            this.creativeId = creativeId
            if (request.format.isAdView)
                request.forBannerAd().onSuccess(this)
            else
                request.forScreenAd().onSuccess(this)
            return
        }

        val casError = when (error.code) {
            CacheError.Code.INTERNET_UNAVAILABLE,
            CacheError.Code.NETWORK_FAILURE ->
                AdError.NO_CONNECTION

            CacheError.Code.NO_AD_FOUND ->
                AdError.NO_FILL

            CacheError.Code.BANNER_DISABLED ->
                AdError(AdErrorCode.CONFIGURATION_ERROR, "Disabled format")

            CacheError.Code.SESSION_NOT_STARTED ->
                AdError.NOT_INITIALIZED

            CacheError.Code.INTERNAL ->
                error.exception?.message?.let {
                    AdError(AdErrorCode.INTERNAL_ERROR, it)
                } ?: AdError(AdErrorCode.REJECTED, "Current country not supported")

            else -> AdError(
                AdErrorCode.INTERNAL_ERROR,
                error.code.name + " " + error.exception
            )
        }
        request.onFailure(casError)
    }

    override fun onAdRequestedToShow(event: ShowEvent) {}

    override fun onAdShown(event: ShowEvent, error: ShowError?) {
        if (error == null) {
            if (ad !is Banner) {
                listener?.onAdShowed(this)
            }
            return
        }
        val casError = when (error.code) {
            ShowError.Code.INTERNET_UNAVAILABLE -> AdError.NO_CONNECTION
            ShowError.Code.SESSION_NOT_STARTED -> AdError.NOT_INITIALIZED
            ShowError.Code.NO_CACHED_AD -> AdError.NOT_READY
            ShowError.Code.AD_ALREADY_VISIBLE -> AdError(AdErrorCode.ALREADY_DISPLAYED)
            else -> AdError(AdErrorCode.INTERNAL_ERROR, error.code.name + " " + error.exception)
        }
        listener?.onAdFailedToShow(this, casError)
    }

    override fun onAdClicked(event: ClickEvent, error: ClickError?) {
        val callback = listener ?: return
        if (error == null)
            callback.onAdClicked(this)
        else
            callback.debug("Ad Click failed: " + error.code.name, Log.WARN)
    }

    override fun onImpressionRecorded(event: ImpressionEvent) {
        creativeId = event.adID
        listener?.onAdImpression(this)
    }

    override fun onAdDismiss(event: DismissEvent) {
        listener?.onAdDismissed(this)
    }

    override fun onRewardEarned(event: RewardEvent) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdExpired(event: ExpirationEvent) {
        listener?.onAdExpired(this)
    }
}