package com.cleveradssolutions.adapters.yango

import com.cleveradssolutions.internal.AdFormatId
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.yandex.mobile.ads.common.BidderTokenLoadListener
import com.yandex.mobile.ads.common.BidderTokenLoader
import com.yandex.mobile.ads.common.BidderTokenRequest

internal class YaSignalCollector(val request: MediationAdSignalRequest) : BidderTokenLoadListener {

    fun collect() {
        val tokenRequest = if (request.format.isAdView) {
            BidderTokenRequest.banner(
                request.forBannerAd().getYaBannerSize()
            )
        } else when (request.format.value) {
            AdFormatId.INTERSTITIAL ->
                BidderTokenRequest.interstitial()

            AdFormatId.REWARDED ->
                BidderTokenRequest.rewarded()

            AdFormatId.APP_OPEN -> {
                if (!request.requirePortraitOrientationOnly()) return
                BidderTokenRequest.appOpenAd()
            }

            AdFormatId.NATIVE ->
                BidderTokenRequest.native()

            else -> {
                onBidderTokenFailedToLoad("Not supported format ${request.format.label}")
                return
            }
        }

        BidderTokenLoader(request.context).loadBidderToken(tokenRequest, this)
    }

    override fun onBidderTokenLoaded(bidderToken: String) {
        request.onSuccess(bidderToken)
    }

    override fun onBidderTokenFailedToLoad(failureReason: String) {
        request.onFailure(AdError(AdErrorCode.INTERNAL_ERROR, failureReason))
    }
}