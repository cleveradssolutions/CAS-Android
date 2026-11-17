package com.cleveradssolutions.adapters.maticoo

import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.zmaticoo.sdk.ads.nativead.AdChoicesView
import com.zmaticoo.sdk.ads.nativead.MediaView
import com.zmaticoo.sdk.ads.nativead.NativeAd

internal class MaticooNativeAd(
    private val ad: NativeAd,
    /** `exp` from bid, or 0 when unknown. */
    expSec: Int
) : MediationNativeAdContent() {
    private var mediaView: MediaView? = null
    private val expiresAt: Long
    private var clickListenerCapture: ClickListenerCaptureView? = null

    override val isExpired: Boolean
        get() = super.isExpired || (expiresAt > 0 && SystemClock.elapsedRealtime() >= expiresAt)

    init {
        headline = ad.headline
        body = ad.body
        callToAction = ad.callToAction
        advertiser = ad.advertiser

        val adIcon = ad.icon
        iconUri = adIcon.uri
        icon = adIcon.drawable

        hasAdChoices = true
        val adMediaContent = ad.mediaContent
        hasVideoContent = adMediaContent.hasVideoContent()
        mediaContentAspectRatio = adMediaContent.aspectRatio

        ad.images?.firstOrNull()?.let {
            mediaImage = it.drawable
            mediaImageUri = it.uri
            if (mediaContentAspectRatio < 0.01) {
                mediaContentAspectRatio = it.aspectRatio
            }
        }

        expiresAt = if (expSec > 0) SystemClock.elapsedRealtime() + expSec * 1000L else 0L
    }

    override fun createAdChoicesContentView(context: Context): View {
        return AdChoicesView(context).also {
            it.setNativeAd(ad)
        }
    }

    override fun createMediaContentView(context: Context): View {
        mediaView?.let { return it }
        return MediaView(context).also {
            mediaView = it
        }
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        // For "native-video" ads a null MediaView fails the first bind (error 30111) with no retry.
        // Image ads fail the same way (30111 from NativeImageComponent) when MediaView is null
        // and the ad has a disclaimer (AdResponse.getDisclaimer() != null).

        // Maticoo SDK (checked 2.0.7.2) binds the ad only once per NativeAd instance:
        // any further bindViews() call is silently ignored, and there is no unbind API.
        // So re-rendering into another view gets no impression/click tracking.

        val clickableViews = assets.clickableViews
        val capture = clickListenerCapture
        if (capture != null) {
            clickableViews.forEach {
                it.setOnClickListener(capture.listener)
            }
        } else {
            clickListenerCapture = ClickListenerCaptureView(view.context).also {
                clickableViews.add(it)
            }
        }
        ad.bindViews(view, mediaView, clickableViews)
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
        clickListenerCapture = null
    }

    /**
     * Never attached to the view hierarchy. Captures the click listener that Maticoo sets
     * in [NativeAd.bindViews], because the SDK binds only once per [NativeAd] instance,
     * so the listener can be reapplied to clickable views on re-render.
     * Re-render restores clicks only: impression tracking stays on the first bound view.
     *
     * Must not be the first clickable view: when MediaView is null, Maticoo tracks
     * impression visibility on `clickableViews[0]`, and a detached view never becomes visible.
     */
    private class ClickListenerCaptureView(context: Context) : View(context) {
        var listener: OnClickListener? = null

        override fun setOnClickListener(l: OnClickListener?) {
            super.setOnClickListener(l)
            if (l != null) {
                listener = l
            }
        }
    }
}