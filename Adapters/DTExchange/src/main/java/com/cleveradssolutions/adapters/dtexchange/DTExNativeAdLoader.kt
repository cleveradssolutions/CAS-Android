package com.cleveradssolutions.adapters.dtexchange

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.fyber.inneractive.sdk.external.InneractiveAdRequest
import com.fyber.inneractive.sdk.external.InneractiveAdSpot
import com.fyber.inneractive.sdk.external.InneractiveAdSpotManager
import com.fyber.inneractive.sdk.external.InneractiveErrorCode
import com.fyber.inneractive.sdk.external.NativeAdContent
import com.fyber.inneractive.sdk.external.NativeAdUnitController
import com.fyber.inneractive.sdk.external.NativeAdVideoContentController

internal class DTExNativeAdLoader(request: MediationNativeAdRequest) :
    InneractiveAdSpot.NativeAdRequestListener(), MediationAdLoader {
    private var request: MediationNativeAdRequest? = request
    private val spot = InneractiveAdSpotManager.get().createSpot()
    private val controller = NativeAdUnitController()

    init {
        val videoController = NativeAdVideoContentController()
        controller.addContentController(videoController)
        spot.addUnitController(controller)
        spot.setRequestListener(this)

        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            spot.loadAd(bidResponse)
        } else {
            spot.requestAd(
                InneractiveAdRequest(request.unitId)
            )
        }
    }

    override fun onInneractiveSuccessfulNativeAdRequest(
        adSpot: InneractiveAdSpot?,
        adContent: NativeAdContent
    ) {
        val request = request ?: return
        this.request = null

        val ad = DTExNativeAd(spot, adContent)
        controller.eventsListener = ad
        request.onSuccess(ad)
    }

    override fun onInneractiveFailedAdRequest(
        adSpot: InneractiveAdSpot?,
        errorCode: InneractiveErrorCode?
    ) {
        request?.onFailure(errorCode.toCASError())
        request = null
    }
}