package com.cleveradssolutions.adapters.maticoo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.zmaticoo.sdk.ads.nativead.AdChoicesView
import com.zmaticoo.sdk.ads.nativead.MediaView
import com.zmaticoo.sdk.ads.nativead.NativeAd

internal class MaticooNativeAd(private val ad: NativeAd) : MediationNativeAdContent() {
    private var mediaView: MediaView? = null

    init {
        headline = ad.headline
        body = ad.body
        callToAction = ad.callToAction
        advertiser = ad.advertiser

        iconUri = ad.icon.uri
        icon = ad.icon.drawable

        hasAdChoices = true
        hasVideoContent = ad.mediaContent.hasVideoContent()
        mediaContentAspectRatio = ad.mediaContent.aspectRatio
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
        ad.bindViews(view, mediaView, assets.clickableViews)
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }
}