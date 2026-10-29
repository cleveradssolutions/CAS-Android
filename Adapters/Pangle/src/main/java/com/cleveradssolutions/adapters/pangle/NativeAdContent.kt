package com.cleveradssolutions.adapters.pangle

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAd
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdData.PAGNativeMediaType.PAGNativeMediaTypeVideo
import com.bytedance.sdk.openadsdk.api.nativeAd.PAGNativeAdInteractionListener
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import kotlin.math.roundToInt

internal class NativeAdContent(
    val nativeAd: PAGNativeAd
) : MediationNativeAdContent(),
    PAGNativeAdInteractionListener {

    init {
        val data = nativeAd.nativeAdData
        headline = data.title
        body = data.description
        callToAction = data.buttonText
        iconUri = data.icon?.imageUrl?.let {
            Uri.parse(it)
        }
        adLabel = null
        hasVideoContent = data.mediaType == PAGNativeMediaTypeVideo
        mediaContentHeightRequired = 0
        hasMediaContent = data.mediaView != null
        hasAdChoices = true // can be null, but hard to check so true
    }

    override fun createAdChoicesContentView(context: Context): View? =
        nativeAd.nativeAdData?.adLogoView?.also {
            val density = context.resources.displayMetrics.density
            it.layoutParams = FrameLayout.LayoutParams(
                (25f * density).roundToInt(),
                (15f * density).roundToInt()
            )
        }

    override fun createMediaContentView(context: Context): View? =
        nativeAd.nativeAdData?.mediaView

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val callToActionViews = ArrayList<View>()
        assets.callToActionView?.let {
            callToActionViews.add(it)
        }

        val dislikeView: View? = null // not used as optional
        nativeAd.registerViewForInteraction(
            view,
            assets.clickableViews,
            callToActionViews,
            dislikeView,
            this
        )
    }

    override fun onAdShowed() {
        // From google mediation adapter sources
        // this callback used for log impression
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdDismissed() {
        // This method is invoked when the ad disappears.
    }
}