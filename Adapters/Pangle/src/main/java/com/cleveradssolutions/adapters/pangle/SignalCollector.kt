package com.cleveradssolutions.adapters.pangle

import com.bytedance.sdk.openadsdk.api.bidding.PAGBiddingRequest
import com.bytedance.sdk.openadsdk.api.init.PAGBidCallback
import com.bytedance.sdk.openadsdk.api.init.PAGBidError
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest

internal class SignalCollector(val request: MediationAdSignalRequest) : PAGBidCallback {

    fun collect() {
        // The user data needs to be set for it to be included in the signals.
        request.applyPAGConfig()

        val paRequest = PAGBiddingRequest()
        paRequest.slotId = request.unitId
        paRequest.adxId = request.getStrParameter(KEY_PAG_ADX)

        if (request.format.isAdView) {
            paRequest.bannerSize = request.forBannerAd().getPAGSize()
        }
        PAGSdk.getBiddingToken(request.context, paRequest, this)
    }

    override fun onBiddingTokenCollected(data: String) {
        request.onSuccess(data)
    }

    override fun onBiddingTokenFailed(error: PAGBidError) {
        request.onFailure(toCASError(error.code, error.message))
    }
}