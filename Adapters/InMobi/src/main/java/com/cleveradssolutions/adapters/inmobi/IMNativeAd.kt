package com.cleveradssolutions.adapters.inmobi

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.inmobi.ads.InMobiNative
import com.inmobi.media.ads.nativeAd.InMobiNativeViewData
import kotlin.math.roundToInt


internal class IMNativeAd(
    private val ad: InMobiNative
) : MediationNativeAdContent() {

    init {
        headline = ad.getAdTitle()
        body = ad.getAdDescription()
        callToAction = ad.getCtaText()
        advertiser = ad.getAdvertiserName()
        starRating = ad.getAdRating().toDouble()
        hasVideoContent = ad.isVideo()
        hasAdChoices = ad.getAdChoiceIcon() != null

        ad.getAdIcon()?.url?.let {
            iconUri = Uri.parse(it)
        }

        // https://support.inmobi.com/monetize/android-guidelines/native-ads-for-android/#set-up-native-ad
        // The default setting is an in-Feed ad layout, an aspect ratio ranging between 256:135 - 1200x627
        mediaContentAspectRatio = ((256.0 / 135.0 + 1200.0 / 627.0) / 2.0).toFloat()

        ad.getMediaView()?.layoutParams?.let {
            val viewHeight = it.height
            if (viewHeight > 0) {
                mediaContentAspectRatio = it.width.toFloat() / viewHeight
            }
        }

        // InMobi 11.1 removed detailed information from json.
        // and this content is empty
        val content = ad.getAdContent()
        if (content != null && content.length() > 0) {
            starRating = content.opt("rating")?.toString()?.toDoubleOrNull()
            price = content.opt("price")?.toString()
            if (content.has("package_name"))
                store = "Google Play"

            content.optJSONObject("screenshots")?.let { image ->
                val ratio = image.optDouble("aspectRatio")
                if (!ratio.isNaN() && ratio > 0) {
                    mediaContentAspectRatio = ratio.toFloat()
                }

                mediaImageUri = image.opt("url")?.toString()
                    ?.ifEmpty { null }
                    ?.let { url -> Uri.parse(url) }
            }

            if (hasVideoContent) {
                content.optJSONObject("video")?.let { video ->
                    val ratio = video.optDouble("aspectRatio")
                    if (!ratio.isNaN() && ratio > 0) {
                        mediaContentAspectRatio = ratio.toFloat()
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                Log.w("CAS.AI", "(D) InMobi customAdContent: $content")
            }
        }
    }

    override val isExpired: Boolean
        get() = super.isExpired || !ad.isReady()

    override fun createAdChoicesContentView(context: Context): View? {
        return ad.getAdChoiceIcon()?.also {
            val density = context.resources.displayMetrics.density
            it.layoutParams = FrameLayout.LayoutParams(
                (20f * density).roundToInt(),
                (15f * density).roundToInt()
            )
        }
    }

    override fun createMediaContentView(context: Context): View? {
        return ad.getMediaView()
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val builder = InMobiNativeViewData.Builder(view)
            .setTitleView(assets.headlineView)
            .setCTAView(assets.callToActionView)
            .setAdvertiserView(assets.advertiserView)
            .setRatingView(assets.starRatingView)
            .setDescriptionView(assets.bodyView)
            .setIconView(assets.iconView)

        ad.registerViewForTracking(builder.build())
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        ad.unTrackViews()
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }
}