package com.cleveradssolutions.adapters.facebook

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdListener
import com.facebook.ads.NativeAdOptionsViewPosition


internal class FacebookNativeAd(
    request: MediationNativeAdRequest
) : MediationNativeAdContent(), MediationAdLoader, NativeAdListener {
    private val ad = NativeAd(request.context, request.unitId)
    private var request: MediationNativeAdRequest? = request
    private var mediaView: MediaView? = null

    init {
        ad.setWatermark(request)
        ad.preferredAdOptionsViewPosition = when (request.adChoicesPlacement) {
            AdChoicesPlacement.TOP_LEFT -> NativeAdOptionsViewPosition.TOP_LEFT
            AdChoicesPlacement.BOTTOM_LEFT -> NativeAdOptionsViewPosition.BOTTOM_LEFT
            AdChoicesPlacement.BOTTOM_RIGHT -> NativeAdOptionsViewPosition.BOTTOM_RIGHT
            else -> NativeAdOptionsViewPosition.TOP_RIGHT
        }
        ad.loadAd(
            ad.buildLoadAdConfig()
                .withAdListener(this)
                .withBid(request.bidResponse)
                .build()
        )
    }

    private fun fillLoadedAdContent() {
        headline = ad.adHeadline
        advertiser = ad.advertiserName
        body = ad.adUntrimmedBodyText
        callToAction = ad.adCallToAction
        mediaImageUri = ad.adCoverImage?.url?.let {
            Uri.parse(it)
        }
        icon = ad.preloadedIconViewDrawable
        iconUri = ad.adIcon?.url?.let {
            Uri.parse(it)
        }
        mediaContentAspectRatio = ad.aspectRatio
        hasVideoContent = ad.adCreativeType == NativeAd.AdCreativeType.VIDEO
        hasAdChoices = true
    }

    override fun createMediaContentView(context: Context): MediaView {
        return mediaView ?: MediaView(context).also {
            // Use listener for onEnterFullscreen
            //it.setListener(this)
            mediaView = it
        }
    }

    override val isExpired: Boolean
        get() = super.isExpired || ad.isAdInvalidated

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val clickableViews = assets.clickableViews
        // Media view is required
        val mediaView = createMediaContentView(view.context)
        clickableViews.add(mediaView)

        ad.registerViewForInteraction(view, mediaView, assets.iconView, clickableViews)
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        ad.unregisterView()
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }

    override fun onAdLoaded(p0: Ad?) {
        val request = request ?: return
        this.request = null
        if (ad.isAdInvalidated) {
            request.onFailure(com.cleversolutions.ads.AdError.EXPIRED)
            return
        }
        fillLoadedAdContent()
        request.onSuccess(this)
    }

    override fun onError(p0: Ad?, error: AdError) {
        request?.onFailure(error.toCASError())
        request = null
    }

    override fun onAdClicked(p0: Ad?) {
        listener?.onAdClicked(this)
    }

    override fun onLoggingImpression(p0: Ad?) {
        listener?.onAdImpression(this)
    }

    override fun onMediaDownloaded(p0: Ad?) {

    }
}