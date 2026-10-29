package com.cleveradssolutions.adapters.pubmatic

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.nativead.POBNativeAd
import com.pubmatic.sdk.nativead.POBNativeAdListener

internal class PubMaticNativeAd(
    private val nativeAd: POBNativeAd
) : MediationNativeAdContent(), POBNativeAdListener {

    init {
        headline = nativeAd.title?.title
        body = nativeAd.description?.value
        advertiser = nativeAd.advertiser?.value
        callToAction = nativeAd.callToAction?.value
        price = nativeAd.price?.value
        starRating = nativeAd.rating?.value?.toDoubleOrNull()
        iconUri = nativeAd.icon?.imageURL?.let { Uri.parse(it) }

        hasAdChoices = null // check late, required main thread

        mediaContentAspectRatio = nativeAd.mediaAspectRatio
        hasVideoContent = mediaContentAspectRatio > 0.0
        nativeAd.mainImage?.let {
            mediaImageUri = Uri.parse(it.imageURL)
            if (!hasVideoContent) {
                mediaContentAspectRatio = it.width.toFloat() / it.height.toFloat()
            }
        }
    }

    override fun createAdChoicesContentView(context: Context): View? {
        return nativeAd.adInfoIcon
    }

    override fun createMediaContentView(context: Context): View? {
        return nativeAd.mediaView
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        nativeAd.registerViewForInteraction(
            container,
            assets.clickableViews,
            this
        )
    }

    override fun destroy() {
        super.destroy()
        nativeAd.destroy()
    }

    override fun onNativeAdImpression(ad: POBNativeAd) {
        listener?.onAdImpression(this)
    }

    override fun onNativeAdClicked(ad: POBNativeAd) {
        listener?.onAdClicked(this)
    }

    override fun onNativeAdClicked(ad: POBNativeAd, assetId: String) {
        listener?.onAdClicked(this)
    }

    override fun onNativeAdRendered(ad: POBNativeAd) {}
    override fun onNativeAdRenderingFailed(ad: POBNativeAd, error: POBError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onNativeAdOpened(ad: POBNativeAd) {
        listener?.onAdShowed(this)
    }

    override fun onNativeAdClosed(ad: POBNativeAd) {
        listener?.onAdDismissed(this)
    }

    override fun onNativeAdExpired(p0: POBNativeAd) {
        setAdExpired()
    }

    override fun onNativeAdLeavingApplication(ad: POBNativeAd) {}
}
