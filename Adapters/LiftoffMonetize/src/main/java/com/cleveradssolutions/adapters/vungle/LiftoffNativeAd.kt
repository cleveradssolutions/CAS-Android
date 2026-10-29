package com.cleveradssolutions.adapters.vungle

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.vungle.ads.BaseAd
import com.vungle.ads.BaseAdListener
import com.vungle.ads.NativeAd
import com.vungle.ads.VungleError
import com.vungle.ads.internal.ui.view.MediaView

internal class LiftoffNativeAd(private val ad: NativeAd) :
    MediationNativeAdContent(), BaseAdListener {

    private var mediaView: MediaView? = null

    init {
        headline = ad.getAdTitle().ifEmpty { null }
        body = ad.getAdBodyText().ifEmpty { null }
        if (ad.hasCallToAction())
            callToAction = ad.getAdCallToActionText().ifEmpty { null }
        starRating = ad.getAdStarRating()
        advertiser = ad.getAdSponsoredText().ifEmpty { null }
        iconUri = ad.getAppIcon().ifEmpty { null }?.let { Uri.parse(it) }

        mediaContentAspectRatio = ad.getMediaAspectRatio()

        hasAdChoices = true
        hasMediaContent = true
        hasVideoContent = ad.hasVideoContent()
        mediaContentHeightRequired = 0
    }

    override fun createMediaContentView(context: Context): MediaView =
        mediaView ?: MediaView(context).also {
            // Vungle update ratio after Loaded callback, so we want check it again
            mediaContentAspectRatio = ad.getMediaAspectRatio()
            mediaView = it
        }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        // Get created or create new media view as required to render
        val mediaView = createMediaContentView(view.context)

        // Vungle required MediaView in clickableViews
        val clickableViews = assets.clickableViews
        clickableViews.add(mediaView)

        // Once the ad registered then canPlayAd() will return false
        // Not check ad.canPlayAd() here, vungle throw onAdFailedToPlay with error

        ad.registerViewForInteraction(
            container as FrameLayout,
            mediaView,
            assets.iconView,
            clickableViews
        )
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        ad.unregisterView()
    }

    override fun onClick(v: View?) {
        ad.performCTA()
    }

    override fun onAdImpression(baseAd: BaseAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked(baseAd: BaseAd) {
        listener?.onAdClicked(this)
    }

    override fun onAdFailedToPlay(baseAd: BaseAd, adError: VungleError) {
        listener?.onAdFailedToShow(this, adError.toCASError())
    }

    override fun onAdFailedToLoad(baseAd: BaseAd, adError: VungleError) {
        onAdFailedToPlay(baseAd, adError)
    }

    override fun onAdLeftApplication(baseAd: BaseAd) {}
    override fun onAdLoaded(baseAd: BaseAd) {}
    override fun onAdStart(baseAd: BaseAd) {}
    override fun onAdEnd(baseAd: BaseAd) {}
}