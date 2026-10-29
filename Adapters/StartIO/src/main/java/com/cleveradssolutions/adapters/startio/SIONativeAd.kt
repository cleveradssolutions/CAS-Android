package com.cleveradssolutions.adapters.startio

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.startapp.sdk.ads.nativead.NativeAdDetails
import com.startapp.sdk.ads.nativead.NativeAdDisplayListener
import com.startapp.sdk.ads.nativead.NativeAdInterface

internal class SIONativeAd(val ad: NativeAdDetails, context: Context) :
    MediationNativeAdContent(), NativeAdDisplayListener {

    private var mediaView: View? = null

    init {
        headline = ad.title.ifEmpty { null }
        body = ad.description.ifEmpty { null }
        callToAction = ad.callToAction.ifEmpty { null }

        if (ad.isApp)
            starRating = ad.rating.toDouble() // return 5.0 by default

        hasVideoContent = false // StartIO not support video

        ad.secondaryImageUrl?.let {
            iconUri = Uri.parse(it)
        }
        ad.secondaryImageBitmap?.let {
            icon = BitmapDrawable(context.resources, it)
        }

        ad.imageUrl?.let {
            mediaImageUri = Uri.parse(it)
        }
        ad.imageBitmap?.let {
            mediaImage = BitmapDrawable(context.resources, it)
            val height = it.height
            if (height > 0) {
                mediaContentAspectRatio = it.width.toFloat() / height.toFloat()
            }
        }

        hasAdChoices = false
        hasMediaContent = iconUri != null
        if (!hasMediaContent) {
            // StartIO have no media or icon image, ad just can have 1 or 2 images.
            // For CAS is priority to Icon image
            iconUri = mediaImageUri
            icon = mediaImage
            mediaImageUri = null
            mediaImage = null
        }
    }

    override fun createMediaContentView(context: Context): View? {
        if (mediaView == null) {
            mediaView = super.createMediaContentView(context)
        }
        return mediaView
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val clickableViews = assets.clickableViews
        mediaView?.let {
            clickableViews.add(it)
        }
        ad.registerViewForInteraction(view, clickableViews, this)
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        ad.unregisterView()
    }

    override fun adDisplayed(p0: NativeAdInterface?) {
        listener?.onAdImpression(this)
    }

    override fun adClicked(p0: NativeAdInterface?) {
        listener?.onAdClicked(this)
    }

    override fun adNotDisplayed(p0: NativeAdInterface?) {

    }

    override fun adHidden(p0: NativeAdInterface?) {

    }
}