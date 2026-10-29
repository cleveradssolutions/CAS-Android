package com.cleveradssolutions.adapters.dtexchange

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAd
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.AdFormat
import com.cleversolutions.ads.AdError
import com.fyber.inneractive.sdk.external.ImpressionData
import com.fyber.inneractive.sdk.external.InneractiveAdRequest
import com.fyber.inneractive.sdk.external.InneractiveAdSpot
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager
import com.fyber.inneractive.sdk.external.InneractiveErrorCode
import com.fyber.inneractive.sdk.external.InneractiveFullScreenAdRewardedListener
import com.fyber.inneractive.sdk.external.InneractiveFullscreenAdEventsListenerWithImpressionData
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController.AdExpiredError
import com.fyber.inneractive.sdk.external.InneractiveFullscreenVideoContentController
import com.fyber.inneractive.sdk.external.InneractiveUnitController

internal class DTExScreenAd(request: MediationScreenAdRequest) : MediationAdBase(),
    MediationAdLoader, MediationScreenAd,
    InneractiveAdSpot.RequestListener,
    InneractiveFullscreenAdEventsListenerWithImpressionData,
    InneractiveFullScreenAdRewardedListener {

    private val view = InneractiveAdSpotManager.get().createSpot()
    private var request: MediationScreenAdRequest? = request

    init {
        val videoController = InneractiveFullscreenVideoContentController()

        val controller = InneractiveFullscreenUnitController()
        controller.addContentController(videoController)
        controller.eventsListener = this
        if (request.format === AdFormat.REWARDED)
            controller.rewardedListener = this

        view.addUnitController(controller)
        view.setRequestListener(this)

        val bidResponse = request.bidResponse
        if (bidResponse != null)
            view.loadAd(bidResponse)
        else
            view.requestAd(InneractiveAdRequest(request.unitId))
    }

    override fun showScreen(listener: MediationAdListener) {
        val controller = view.selectedUnitController as? InneractiveFullscreenUnitController
        if (controller == null || !view.isReady) {
            listener.onAdFailedToShow(this, AdError.NOT_READY)
            return
        }
        val activity = listener.requireUIContext(this) ?: return
        controller.show(activity)
    }

    override fun destroy() {
        super.destroy()
        // Can be called in background thread
        view.destroy()
    }

    override fun onInneractiveSuccessfulAdRequest(p0: InneractiveAdSpot?) {
        request?.onSuccess(this)
        request = null
    }

    override fun onInneractiveFailedAdRequest(
        spot: InneractiveAdSpot?,
        error: InneractiveErrorCode?
    ) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdEnteredErrorState(
        spot: InneractiveAdSpot?,
        error: InneractiveUnitController.AdDisplayError?
    ) {
        val casError = if (error is AdExpiredError)
            AdError.EXPIRED
        else
            AdError(AdErrorCode.INTERNAL_ERROR, error.toString())
        listener?.onAdFailedToShow(this, casError)
    }

    override fun onAdImpression(spot: InneractiveAdSpot?, data: ImpressionData) {
        recordImpression(data)
    }

    override fun onAdClicked(p0: InneractiveAdSpot?) {
        listener?.onAdClicked(this)
    }

    override fun onAdDismissed(p0: InneractiveAdSpot?) {
        listener?.onAdDismissed(this)
    }

    override fun onAdRewarded(p0: InneractiveAdSpot?) {
        listener?.onUserEarnedReward(this)
    }

    override fun onAdImpression(p0: InneractiveAdSpot?) {
    }

    override fun onAdWillCloseInternalBrowser(p0: InneractiveAdSpot?) {
    }

    override fun onAdWillOpenExternalApp(p0: InneractiveAdSpot?) {
    }
}