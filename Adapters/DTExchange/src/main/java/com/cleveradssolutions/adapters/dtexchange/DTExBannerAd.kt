package com.cleveradssolutions.adapters.dtexchange

import android.view.View
import android.widget.RelativeLayout
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.fyber.inneractive.sdk.external.ImpressionData
import com.fyber.inneractive.sdk.external.InneractiveAdRequest
import com.fyber.inneractive.sdk.external.InneractiveAdSpot
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListenerWithImpressionData
import com.fyber.inneractive.sdk.external.InneractiveAdViewUnitController
import com.fyber.inneractive.sdk.external.InneractiveErrorCode
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController.AdExpiredError
import com.fyber.inneractive.sdk.external.InneractiveUnitController

internal class DTExBannerAd(request: MediationBannerAdRequest) : MediationAdBase(),
    MediationAdLoader, MediationBannerAd,
    InneractiveAdSpot.RequestListener,
    InneractiveAdViewEventsListenerWithImpressionData {

    private var request: MediationBannerAdRequest? = request
    private var spot = InneractiveAdSpotManager.get().createSpot()

    init {
        val controller = InneractiveAdViewUnitController()
        controller.eventsListener = this

        spot.addUnitController(controller)
        spot.setRequestListener(this)

        val bidResponse = request.bidResponse
        if (bidResponse != null)
            spot.loadAd(bidResponse)
        else
            spot.requestAd(InneractiveAdRequest(request.unitId))
    }

    override fun destroy() {
        super.destroy()
        // Can be called in background thread
        spot.destroy()
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val controller = spot.selectedUnitController as? InneractiveAdViewUnitController
        if (controller == null || !spot.isReady) {
            throw NullPointerException("Not ready")
        }
        val group = RelativeLayout(request.context)
        group.layoutParams = request.createAdLayout()
        controller.bindView(group)
        return group
    }

    override fun onInneractiveSuccessfulAdRequest(spot: InneractiveAdSpot?) {
        val request = request ?: return
        this.request = null
        if (this.spot.isReady) {
            request.onSuccess(this)
        } else {
            request.onFailure(AdError.NO_FILL)
        }
    }

    override fun onInneractiveFailedAdRequest(
        spot: InneractiveAdSpot?,
        error: InneractiveErrorCode?
    ) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdImpression(spot: InneractiveAdSpot?, data: ImpressionData) {
        recordImpression(data)
    }

    override fun onAdClicked(p0: InneractiveAdSpot?) {
        listener?.onAdClicked(this)
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

    override fun onAdExpanded(p0: InneractiveAdSpot?) {
        listener?.onAdShowed(this)
    }

    override fun onAdCollapsed(p0: InneractiveAdSpot?) {
        listener?.onAdDismissed(this)
    }

    override fun onAdImpression(p0: InneractiveAdSpot?) {
    }

    override fun onAdResized(p0: InneractiveAdSpot?) {
    }

    override fun onAdWillCloseInternalBrowser(p0: InneractiveAdSpot?) {
    }

    override fun onAdWillOpenExternalApp(p0: InneractiveAdSpot?) {
    }
}