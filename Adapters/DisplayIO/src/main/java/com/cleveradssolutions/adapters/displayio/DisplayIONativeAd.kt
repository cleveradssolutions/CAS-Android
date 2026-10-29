package com.cleveradssolutions.adapters.displayio

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.brandio.ads.ads.Ad
import com.brandio.ads.ads.supers.NativeAdInterface
import com.brandio.ads.components.NativeMediaView
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.listeners.AdEventListener
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews

internal class DisplayIONativeAd(private val ad: Ad, private val nativeAd: NativeAdInterface)
    : MediationNativeAdContent(), AdEventListener {

    private var mediaView: NativeMediaView? = null

    init {
        headline = nativeAd.headline
        body = nativeAd.body
        callToAction = nativeAd.callToAction
        advertiser = nativeAd.advertiser
        price = nativeAd.price

        icon = nativeAd.iconDrawable
        iconUri = nativeAd.iconUri

        hasVideoContent = nativeAd.hasVideoContent()
        hasMediaContent = true
        hasAdChoices = nativeAd.privacy != null

        ad.setEventListener(this)
    }

    override fun createAdChoicesContentView(context: Context): View? {
        val privacyUrl = nativeAd.privacy
        if (privacyUrl.isNullOrBlank()) {
            return null
        }

        return createAdChoicesContent(context, privacyUrl)
    }

    override fun createMediaContentView(context: Context): View {
        return mediaView ?: NativeMediaView(context).also {
            mediaView = it
        }
    }

    override fun onRenderedInView(view: ViewGroup, container: ViewGroup, assets: NativeAdAssetViews, listener: MediationAdListener) {
        val media = createMediaContentView(view.context) as NativeMediaView
        nativeAd.registerViewForInteraction(view, media, assets.iconView, assets.headlineView, assets.callToActionView)
    }

    override fun onDetachFromView(view: ViewGroup, container: ViewGroup, assets: NativeAdAssetViews) {
        nativeAd.unregisterView()
        super.onDetachFromView(view, container, assets)
    }

    override fun destroy() {
        super.destroy()
        mediaView = null

        CASHandler.main {
            ad.close()
        }
    }

    override fun onShown(ad: Ad) {
        listener?.onAdImpression(this)
    }

    override fun onClicked(ad: Ad) {
        listener?.onAdClicked(this)
    }

    override fun onFailedToShow(ad: Ad, error: DIOError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onClosed(ad: Ad) {
    }

    override fun onAdCompleted(ad: Ad) {
    }
}