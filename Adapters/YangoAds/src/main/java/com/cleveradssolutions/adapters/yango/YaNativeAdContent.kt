package com.cleveradssolutions.adapters.yango

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout.LayoutParams
import android.widget.ImageView
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.cleversolutions.ads.AdError
import com.yandex.mobile.ads.common.AdBindingResult
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.nativeads.MediaView
import com.yandex.mobile.ads.nativeads.NativeAd
import com.yandex.mobile.ads.nativeads.NativeAdEventListener
import com.yandex.mobile.ads.nativeads.NativeAdView
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder
import kotlin.math.roundToInt

internal class YaNativeAdContent(val ad: NativeAd) :
    MediationNativeAdContent(), NativeAdEventListener {
    private var mediaView: MediaView? = null

    init {
        ad.setNativeAdEventListener(this)

        creativeId = ad.adInfo.findCreativeId()

        // Required assets:
        // title, domain, warning, sponsored, feedback, callToAction, media, icon, price

        val assets = ad.adAssets
        headline = assets.title
        // Yandex Ads have sponsored and domain in same ad, and required present it
        advertiser = assets.domain
        adLabel = assets.sponsored // assets.age included to sponsored
        callToAction = assets.callToAction
        price = assets.price
        // Body content is optional, but required present Warning content to user
        val warning = assets.warning?.value
        body = warning ?: assets.body

        // The Icon can be nil for not app content, but the favicon is too small to use as icon asset.
        assets.icon?.drawable?.let {
            icon = it
        }

        assets.image?.drawable?.let {
            mediaImage = it
        }

        hasAdChoices = ad.adAssets.isFeedbackAvailable
        hasMediaContent = false
        mediaContentHeightRequired = 0
        val media = assets.media
        if (media != null) {
            hasMediaContent = true
            hasVideoContent = media.hasVideo
            mediaContentAspectRatio = media.aspectRatio
        } else {
            mediaImage?.let {
                hasMediaContent = true
                val height = it.intrinsicHeight
                if (height > 0) {
                    mediaContentAspectRatio =
                        it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
                }
            }
        }

        // Optional assets:
        starRating = assets.rating?.toDouble()
        reviewCount = assets.reviewCount
    }

    override fun createAdChoicesContentView(context: Context): View? =
        if (ad.adAssets.isFeedbackAvailable)
            ImageView(context).also {
                it.scaleType = ImageView.ScaleType.FIT_CENTER
                val density = context.resources.displayMetrics.density
                it.layoutParams = LayoutParams(
                    (15f * density).roundToInt(),
                    (18f * density).roundToInt()
                )
            }
        else null

    // Media View can be null and not presented if media has not video and no image
    override fun createMediaContentView(context: Context): View? {
        if (mediaView == null && hasMediaContent) {
            mediaView = MediaView(context)
        }
        return mediaView
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        val yaView = NativeAdView(view.context)
        view.removeView(container)
        yaView.addView(container)
        view.addView(yaView)

        // Attention! Required all views below
        val binder = NativeAdViewBinder.Builder(yaView)
            .setTitleView(assets.headlineView)
            .setDomainView(assets.advertiserView)
            .setCallToActionView(assets.callToActionView)
            .setPriceView(assets.priceView)
            .setSponsoredView(assets.adLabelView)
            .setReviewCountView(assets.reviewCountView)
            .setIconView(assets.iconView)
            .setMediaView(mediaView)
            .setFeedbackView(assets.adChoicesView?.getChildAt(0) as? ImageView)

        // RatingView required implement Yandex Rating interface
        //binder.setRatingView(assets.starRatingView)

        assets.bodyView?.let {
            if (it.text?.toString() == ad.adAssets.warning?.value)
                binder.setWarningView(it)
            else
                binder.setBodyView(it)
        }

        when (val result = ad.bindNativeAd(binder.build())) {
            is AdBindingResult.Success -> {
                yaView.visibility = View.VISIBLE
            }

            is AdBindingResult.Failure -> {
                val error = result.missingAssetName?.let {
                    AdError(AdErrorCode.REJECTED, "Missing asset view for $it")
                } ?: AdError(result.exception.message)
                listener.onAdFailedToShow(this, error)
            }
        }
    }

    @SuppressLint("WrongThread")
    override fun destroy() {
        super.destroy()
        ad.setNativeAdEventListener(null)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onImpression(impressionData: ImpressionData?) {
        onYaImpression(impressionData)
    }

}