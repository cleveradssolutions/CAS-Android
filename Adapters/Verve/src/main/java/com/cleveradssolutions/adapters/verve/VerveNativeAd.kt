package com.cleveradssolutions.adapters.verve

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import net.pubnative.lite.sdk.models.NativeAd

internal class VerveNativeAd(ad: NativeAd, context: Context) : MediationNativeAdContent(),
    NativeAd.Listener {
    private var nativeAd: NativeAd? = ad

    init {
        headline = ad.title
        body = ad.description
        callToAction = ad.callToActionText
        creativeId = ad.creativeId

        ad.rating.takeIf { it > 0 }?.let {
            starRating = it.toDouble()
        }

        iconUri = ad.iconUrl?.let { Uri.parse(it) }
        icon = ad.iconBitmap?.let {
            BitmapDrawable(context.resources, it)
        }

        mediaImageUri = ad.bannerUrl?.let { Uri.parse(it) }
        mediaImage = ad.bannerBitmap?.let {
            if (it.height > 0) {
                mediaContentAspectRatio = it.width.toFloat() / it.height
            }
            BitmapDrawable(context.resources, it)
        }

        hasMediaContent = mediaImage != null || mediaImageUri != null
        hasVideoContent = false
        mediaContentHeightRequired = 0
    }

    override val isExpired: Boolean
        get() = super.isExpired || nativeAd == null

    override fun createAdChoicesContentView(context: Context): View? {
        return nativeAd?.getContentInfo(context)
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val ad = nativeAd!!
        val clickableViews = assets.clickableViews
        assets.mediaView?.let {
            clickableViews.add(it)
        }

        ad.startTracking(container, this)
        clickableViews.forEach {
            ad.startTrackingClicks(it)
        }
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        nativeAd?.stopTracking()
        assets.clickableViews.forEach {
            it.setOnClickListener(null)
        }
    }

    override fun onAdImpression(ad: NativeAd?, view: View?) {
        listener?.onAdImpression(this)
    }

    override fun onAdClick(ad: NativeAd?, view: View?) {
        listener?.onAdClicked(this)
    }

    override fun destroy() {
        super.destroy()

        CASHandler.main {
            nativeAd?.stopTracking()
            nativeAd = null
        }
    }

}
