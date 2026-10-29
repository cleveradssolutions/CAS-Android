package com.cleveradssolutions.adapters.bigo

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdInteractionListener
import sg.bigo.ads.api.AdOptionsView
import sg.bigo.ads.api.AdTag
import sg.bigo.ads.api.MediaView
import sg.bigo.ads.api.NativeAd
import kotlin.math.roundToInt

internal class BigoNativeContent(ad: NativeAd) :
    MediationNativeAdContent(), AdInteractionListener {
    private var ad: NativeAd? = ad
    private var mediaView: MediaView? = null

    init {
        headline = ad.title
        body = ad.description
        callToAction = ad.callToAction

        val warning: String? = ad.warning
        advertiser = if (warning.isNullOrEmpty())
            ad.advertiser.ifEmpty { null }
        else
            ad.warning
        hasVideoContent = ad.creativeType == NativeAd.CreativeType.VIDEO
        hasAdChoices = true

        if (ad.hasIcon()) {
            icon = ColorDrawable(Color.TRANSPARENT)
        }
        mediaContentHeightRequired = 0
        mediaContentAspectRatio = ad.mediaContentAspectRatio

        creativeId = ad.creativeId
    }

    override fun createMediaContentView(context: Context): View =
        mediaView ?: MediaView(context).also { mediaView = it }

    override fun createAdChoicesContentView(context: Context): View =
        AdOptionsView(context).also {
            val density = context.resources.displayMetrics.density
            val size = (20f * density).roundToInt()
            it.layoutParams = FrameLayout.LayoutParams(size, size)
        }

    override val isExpired: Boolean
        get() = super.isExpired || ad.let { it == null || it.isExpired }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val ad = ad ?: throw UnsupportedOperationException()
        if (ad.isExpired) {
            listener.onAdExpired(this)
            return
        }

        assets.headlineView?.tag = AdTag.TITLE
        assets.bodyView?.tag = AdTag.DESCRIPTION
        assets.callToActionView?.tag = AdTag.CALL_TO_ACTION
        assets.advertiserView?.let {
            it.tag = if (ad.warning.isEmpty()) AdTag.SPONSORED_LABEL else AdTag.WARNING
        }

        val optionsView = assets.adChoicesView?.getChildAt(0) as? AdOptionsView
        ad.registerViewForInteraction(
            view,
            mediaView,
            assets.iconView,
            optionsView,
            assets.clickableViews
        )
    }

    override fun destroy() {
        super.destroy()
        ad?.destroy()
        ad = null
    }

    override fun onAdError(error: AdError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdImpression() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdOpened() {
    }

    override fun onAdClosed() {
    }
}