package com.cleveradssolutions.adapters.startio

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.adlisteners.VideoListener

internal class StartIOScreenAd(
    request: MediationScreenAdRequest,
    mode: StartAppAd.AdMode
) : MediationAdBase(), MediationAdLoader, MediationScreenAd,
    AdEventListener, AdDisplayListener, VideoListener {
    private var request: MediationScreenAdRequest? = request
    private var view: StartAppAd? = null

    init {
        val preferences = request.createSIOPreferences()

        if (mode == StartAppAd.AdMode.REWARDED_VIDEO)
            preferences.type = Ad.AdType.REWARDED_VIDEO
        if (CAS.settings.mutedAdSounds)
            preferences.muteVideo()

        val newView = StartAppAd(request.context)
        if (mode == StartAppAd.AdMode.REWARDED_VIDEO)
            newView.setVideoListener(this)
        view = newView

        newView.loadAd(
            mode, preferences, this, request.bidResponse
        )
    }

    override fun showScreen(listener: MediationAdListener) {
        val view = view
        if (view == null) {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
            return
        }
        listener.contextService.getActivityOrNull()?.let {
            view.context = it
        }
        view.showAd(this)
    }

    override fun destroy() {
        super.destroy()
        view?.setVideoListener(null)
        view = null
    }

    override fun onReceiveAd(ad: Ad) {
        creativeId = ad.adId
        request?.onSuccess(this)
        request = null
    }

    override fun onFailedToReceiveAd(ad: Ad?) {
        disableAutoRetries()
        request?.onFailure(getCASError(ad?.errorMessage, ad?.isBelowMinCPM))
        request = null
    }

    override fun adDisplayed(ad: Ad?) {
        listener?.onAdImpression(this)
    }

    override fun adNotDisplayed(ad: Ad?) {
        listener?.onAdFailedToShow(this, AdError(AdErrorCode.INTERNAL_ERROR, ad?.errorMessage))
    }

    override fun adClicked(ad: Ad?) {
        listener?.onAdClicked(this)
    }

    override fun onVideoCompleted() {
        listener?.onUserEarnedReward(this)
    }

    override fun adHidden(ad: Ad?) {
        listener?.onAdDismissed(this)
    }
}