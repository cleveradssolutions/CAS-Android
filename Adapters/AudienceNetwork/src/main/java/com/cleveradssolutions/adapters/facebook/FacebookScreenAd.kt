package com.cleveradssolutions.adapters.facebook

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.facebook.ads.Ad
import com.facebook.ads.FullScreenAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.RewardedVideoAdListener

internal class FacebookScreenAd(
    request: MediationScreenAdRequest,
    private val view: FullScreenAd
) : MediationAdBase(), MediationScreenAd, MediationAdLoader,
    InterstitialAdListener, RewardedVideoAdListener {
    private var request: MediationScreenAdRequest? = request

    override fun showScreen(listener: MediationAdListener) {
        // Check if ad is already expired or invalidated, and do not show ad if that is
        // the case. You will not get paid to show an invalidated ad.
        if (view.isAdInvalidated) {
            listener.onAdFailedToShow(this, AdError.EXPIRED)
            return
        }
        view.show()
    }

    override fun destroy() {
        super.destroy()
        CASHandler.main {
            view.destroy()
        }
    }

    override fun onError(ad: Ad, error: FacebookError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdLoaded(ad: Ad?) {
        request?.onSuccess(this)
        request = null
    }

    override fun onAdClicked(ad: Ad?) {
        listener?.onAdClicked(this)
    }

    override fun onLoggingImpression(ad: Ad?) {
        listener?.onAdImpression(this)
    }

    override fun onInterstitialDisplayed(ad: Ad?) {
        listener?.onAdShowed(this)
    }

    override fun onInterstitialDismissed(ad: Ad?) {
        listener?.onAdDismissed(this)
    }

    override fun onRewardedVideoCompleted() {
        listener?.onUserEarnedReward(this)
    }

    override fun onRewardedVideoClosed() {
        listener?.onAdDismissed(this)
    }

}