package com.cleveradssolutions.adapters.dtexchange

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.cleversolutions.ads.AdError
import com.fyber.inneractive.sdk.external.ImpressionData
import com.fyber.inneractive.sdk.external.InneractiveAdSpot
import com.fyber.inneractive.sdk.external.InneractiveAdViewEventsListenerWithImpressionData
import com.fyber.inneractive.sdk.external.InneractiveFullscreenUnitController.AdExpiredError
import com.fyber.inneractive.sdk.external.InneractiveUnitController
import com.fyber.inneractive.sdk.external.MediaView
import com.fyber.inneractive.sdk.external.NativeAdContent

internal class DTExNativeAd(
    private val spot: InneractiveAdSpot,
    private val content: NativeAdContent
) : MediationNativeAdContent(), InneractiveAdViewEventsListenerWithImpressionData {

    init {
        headline = content.adTitle
        body = content.adDescription
        advertiser = content.advertiserName
        callToAction = content.adCallToAction
        iconUri = content.appIcon
        starRating = content.rating?.toDouble()
        price = content.price
        content.mediaAspectRatio?.let {
            mediaContentAspectRatio = it
        }

        hasVideoContent = spot.adContent?.isVideoAd == true
        hasAdChoices = false
    }

    override fun createMediaContentView(context: Context): View {
        content.mediaView?.let {
            return it
        }
        val view = MediaView(context)
        content.bindMediaView(view)
        return view
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val assetList = assets.clickableViews
        container.tag = NativeAdContent.ViewTag.ROOT

        assets.iconView?.tag = NativeAdContent.ViewTag.AD_ICON
        assets.callToActionView?.tag = NativeAdContent.ViewTag.CTA
        assets.headlineView?.tag = NativeAdContent.ViewTag.AD_TITLE
        assets.bodyView?.tag = NativeAdContent.ViewTag.AD_DESCRIPTION
        assets.starRatingView?.tag = NativeAdContent.ViewTag.RATING

        val mediaView = assets.mediaView?.getChildAt(0) as? MediaView
        if (mediaView != null) {
            mediaView.tag = NativeAdContent.ViewTag.MEDIA_VIEW
        }

        content.registerViewsForInteraction(
            container,
            mediaView,
            assets.iconView,
            assetList
        )
    }

    override fun destroy() {
        super.destroy()

        spot.destroy()
    }

    override fun onAdImpression(
        spot: InneractiveAdSpot?,
        impressionData: ImpressionData
    ) {
        recordImpression(impressionData)
    }

    override fun onAdImpression(p0: InneractiveAdSpot?) {
    }

    override fun onAdClicked(p0: InneractiveAdSpot?) {
        listener?.onAdClicked(this)
    }

    override fun onAdWillCloseInternalBrowser(p0: InneractiveAdSpot?) {
    }

    override fun onAdWillOpenExternalApp(p0: InneractiveAdSpot?) {
    }

    override fun onAdEnteredErrorState(
        p0: InneractiveAdSpot?,
        error: InneractiveUnitController.AdDisplayError?
    ) {
        val casError = if (error is AdExpiredError)
            AdError.EXPIRED
        else
            AdError(AdErrorCode.INTERNAL_ERROR, error.toString())
        listener?.onAdFailedToShow(this, casError)
    }

    override fun onAdExpanded(p0: InneractiveAdSpot?) {
    }

    override fun onAdResized(p0: InneractiveAdSpot?) {
    }

    override fun onAdCollapsed(p0: InneractiveAdSpot?) {
    }
}