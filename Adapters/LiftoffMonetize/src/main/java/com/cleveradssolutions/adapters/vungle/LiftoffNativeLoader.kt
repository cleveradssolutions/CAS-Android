package com.cleveradssolutions.adapters.vungle

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.vungle.ads.BaseAd
import com.vungle.ads.BaseAdListener
import com.vungle.ads.NativeAd
import com.vungle.ads.VungleError

internal class LiftoffNativeLoader(request: MediationNativeAdRequest) : MediationAdLoader, BaseAdListener {
    private var request: MediationNativeAdRequest? = request
    private var nativeAd: NativeAd? = null

    init {
        // Vungle need activity context to show GDPR dialog(if required)
        // and launch Google Play Store
        val context = request.uiContext ?: request.context

        nativeAd = NativeAd(context, request.unitId).also {
            it.adListener = this
            it.adOptionsPosition = request.adChoicesPlacement
            it.videoOptions.startMuted = request.isStartVideoMuted
            it.load(request.bidResponse)
        }
    }

    override fun onAdLoaded(baseAd: BaseAd) {
        val request = request ?: return
        val nativeAd = nativeAd ?: return
        this.request = null
        this.nativeAd = null
        val content = LiftoffNativeAd(nativeAd)
        content.creativeId = nativeAd.creativeId
        nativeAd.adListener = content
        request.onSuccess(content)
    }

    override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
        request?.onFailure(adError.toCASError())
        request = null
        nativeAd = null
    }

    // Ad listener implemented in NativeAdContent
    override fun onAdClicked(baseAd: BaseAd) {}
    override fun onAdEnd(baseAd: BaseAd) {}
    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {}
    override fun onAdImpression(baseAd: BaseAd) {}
    override fun onAdLeftApplication(baseAd: BaseAd) {}
    override fun onAdStart(baseAd: BaseAd) {}
}