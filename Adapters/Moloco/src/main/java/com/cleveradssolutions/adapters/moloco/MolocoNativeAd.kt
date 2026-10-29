package com.cleveradssolutions.adapters.moloco

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.moloco.sdk.publisher.NativeAd

internal class MolocoNativeAd(
    private val nativeAd: NativeAd,
    assets: NativeAd.Assets
) : MediationNativeAdContent(), NativeAd.InteractionListener {

    private var sdkMediaView: View? = assets.mediaView

    init {
        nativeAd.interactionListener = this
        headline = assets.title
        body = assets.description
        advertiser = assets.sponsorText
        callToAction = assets.callToActionText
        starRating = assets.rating?.toDouble()
        iconUri = assets.iconUri
        mediaImageUri = assets.mainImageUri

        hasAdChoices = false
        hasVideoContent = sdkMediaView != null
        hasMediaContent = sdkMediaView != null || mediaImageUri != null
    }

    override fun createMediaContentView(context: Context): View? {
        sdkMediaView?.let { view ->
            return view
        }
        return super.createMediaContentView(context)
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        overrideClickHandling(assets.clickableViews)
        overrideImpressionHandling(view)
    }

    override fun onClick(v: View?) {
        nativeAd.handleGeneralAdClick()
    }

    override fun onViewImpression(view: View) {
        nativeAd.handleImpression()
    }

    override fun destroy() {
        super.destroy()
        sdkMediaView = null
        nativeAd.destroy()
    }

    override fun onImpressionHandled() {
        listener?.onAdImpression(this)
    }

    override fun onGeneralClickHandled() {
        listener?.onAdClicked(this)
    }
}