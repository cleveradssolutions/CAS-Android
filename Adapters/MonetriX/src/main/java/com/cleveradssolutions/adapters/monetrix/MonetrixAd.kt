package com.cleveradssolutions.adapters.monetrix

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleversolutions.ads.AdError
import com.monetrix.adsdk.api.banner.BannerAd
import com.monetrix.adsdk.api.base.Ad
import com.monetrix.adsdk.api.base.AdListener
import com.monetrix.adsdk.api.interstitial.InstlAd
import com.monetrix.adsdk.api.reward.RwdAd
import com.monetrix.adsdk.api.reward.RwdAdListener

internal class MonetrixAd(
    val ad: Ad
) : MediationAdBase(), MediationScreenAd, MediationBannerAd,
    AdListener, RwdAdListener {

    override val isExpired: Boolean
        get() = super.isExpired || ad.hasExpired()

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = (ad as BannerAd).adView()
        view.layoutParams = request.createAdLayout()
        ad.setAdListener(this)
        return view
    }

    override fun showScreen(listener: MediationAdListener) {
        when (ad) {
            is RwdAd -> {
                ad.setAdListener(this as RwdAdListener)
                ad.show()
            }

            is InstlAd -> {
                ad.setAdListener(this)
                ad.show()
            }

            else -> {
                listener.onAdFailedToShow(this, AdError("Loaded not screen ad"))
            }
        }
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }

    override fun onAdError(error: MXError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdOpened() {
        listener?.onAdShowed(this)
    }

    override fun onAdDisplayed() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdClosed() {
        listener?.onAdDismissed(this)
    }

    override fun onAdRewarded() {
        listener?.onUserEarnedReward(this)
    }
}