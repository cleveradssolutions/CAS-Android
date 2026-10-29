package com.cleveradssolutions.adapters.unity

import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.unity3d.ads.AdExpiredListener
import com.unity3d.ads.BannerAd
import com.unity3d.ads.BannerShowListener
import com.unity3d.ads.LoadListener
import com.unity3d.ads.UnityAdsError


internal class UnityBannerAd(
    request: MediationBannerAdRequest
) : MediationAdBase(),
    MediationBannerAd,
    MediationAdLoader,
    BannerShowListener,
    LoadListener<BannerAd>,
    AdExpiredListener<BannerAd> {

    private var bannerAd: BannerAd? = null
    private var request: MediationBannerAdRequest? = request

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = bannerAd!!.view
        view.layoutParams = request.createAdaptiveAdLayout()
        return view
    }

    override fun destroy() {
        super.destroy()
        bannerAd = null
        request = null
    }

    override fun onAdLoaded(unityAd: BannerAd?, error: UnityAdsError?) {
        val request = request ?: return
        this.request = null
        if (unityAd != null) {
            bannerAd = unityAd
            unityAd.onAdExpired = this
            request.onSuccess(this)
        } else {
            request.onFailure(mapUnityError(error))
        }
    }

    override fun onImpression(banner: BannerAd) {
        listener?.onAdImpression(this)
    }

    override fun onClicked(banner: BannerAd) {
        listener?.onAdClicked(this)
    }

    override fun onFailedToShow(banner: BannerAd, error: UnityAdsError) {
        listener?.onAdFailedToShow(this, mapUnityError(error))
    }

    override fun onAdExpired(ad: BannerAd) {
        listener?.onAdExpired(this)
    }
}