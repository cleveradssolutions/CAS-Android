package com.cleveradssolutions.adapters.vungle

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.vungle.ads.BaseAd
import com.vungle.ads.BaseFullscreenAd
import com.vungle.ads.InterstitialAd
import com.vungle.ads.RewardedAdListener
import com.vungle.ads.VungleError

internal open class LiftoffInterstitialAd(
    val view: BaseFullscreenAd,
    request: MediationScreenAdRequest
) : MediationAdBase(),
    RewardedAdListener, MediationScreenAd, MediationAdLoader {
    private var request: MediationScreenAdRequest? = request

    init {
        view.adListener = this
        view.load(request.bidResponse)
    }

    constructor(request: MediationScreenAdRequest)
            : this(InterstitialAd(request.context, request.unitId), request)

    override fun showScreen(listener: MediationAdListener) {
        // view.canPlayAd() check internal and fire onAdFailedToPlay
        // Vungle SDK can get application context from constructor to play ad if activity is null
        view.play(listener.contextService.getActivityOrNull())
    }

    override fun destroy() {
        super.destroy()
        view.adListener = null
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

    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
        listener?.onAdFailedToShow(this, adError.toCASError())
    }

    override fun onAdStart(baseAd: BaseAd) {
        listener?.onAdShowed(this)
    }

    override fun onAdImpression(baseAd: BaseAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked(baseAd: BaseAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdEnd(baseAd: BaseAd) {
        listener?.onAdDismissed(this)
    }

    override fun onAdRewarded(baseAd: BaseAd) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdLeftApplication(baseAd: BaseAd) {

    }
}