package com.cleveradssolutions.adapters.vungle

import android.view.View
import android.view.ViewGroup.LayoutParams
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoaderUI
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.vungle.ads.BannerAdListener
import com.vungle.ads.BaseAd
import com.vungle.ads.VungleAdSize
import com.vungle.ads.VungleAds
import com.vungle.ads.VungleBannerView
import com.vungle.ads.VungleError

internal class LiftoffBannerAd : MediationAdBase(),
    BannerAdListener, MediationBannerAd, MediationAdLoaderUI {
    private var view: VungleBannerView? = null
    private var request: MediationBannerAdRequest? = null

    override fun loadAdUI(request: MediationAdUnitRequest) {
        val bannerRequest = request.forBannerAd()
        this.request = bannerRequest
        // Attention! Vungle support adaptive size by new Inline AdUnit format only.
        val adSize = bannerRequest.adSize
        val layoutParams: LayoutParams
        val vungleSize = if ((adSize.isAdaptive || adSize.isInline)
            && VungleAds.isInline(request.unitId)
        ) {
            layoutParams = bannerRequest.createAdaptiveAdLayout()
            VungleAdSize.getAdSizeWithWidthAndHeight(adSize.width, adSize.height)
        } else {
            layoutParams = bannerRequest.createAdLayout()
            when (bannerRequest.adSizeId) {
                AdSizeId.LARGE -> VungleAdSize.BANNER_LEADERBOARD
                AdSizeId.MREC -> VungleAdSize.MREC
                else -> VungleAdSize.BANNER
            }
        }

        view =
            VungleBannerView(request.context, request.unitId, vungleSize).also {
                it.adListener = this
                it.layoutParams = layoutParams
                it.load(request.bidResponse)
            }
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View = view!!

    override fun destroy() {
        super.destroy()
        /**
         * Required main thread
         * A WebView method was called on thread 'CASHandler-51'. All WebView methods must be called on the same thread. (Expected Looper Looper (main, tid 2) {f1c4a98} called on null, FYI main Looper is Looper (main, tid 2) {f1c4a98})
         */
        view?.let {
            view = null
            it.adListener = null
            CASHandler.main {
                it.finishAd()
            }
        }
    }

    override fun onAdLoaded(baseAd: BaseAd) {
        creativeId = baseAd.creativeId
        request?.onSuccess(this)
        request = null
    }

    override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
        request?.onFailure(adError.toCASError())
        request = null
    }

    override fun onAdClicked(baseAd: BaseAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
        listener?.onAdFailedToShow(this, adError.toCASError())
    }

    override fun onAdImpression(baseAd: BaseAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdStart(baseAd: BaseAd) {}
    override fun onAdEnd(baseAd: BaseAd) {}
    override fun onAdLeftApplication(baseAd: BaseAd) {}
}