package com.cleveradssolutions.adapters.monetrix

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.monetrix.adsdk.api.adview.AdChoiceView
import com.monetrix.adsdk.api.adview.AdMediaView
import com.monetrix.adsdk.api.base.AdError
import com.monetrix.adsdk.api.base.AdListener
import com.monetrix.adsdk.api.nativead.NativeAd
import com.monetrix.adsdk.api.nativead.NativeAd.CreativeType
import com.monetrix.adsdk.api.nativead.NativeAdViewBinder

internal class MonetrixNativeAd(
    private val ad: NativeAd
) : MediationNativeAdContent(), AdListener {

    init {
        creativeId = ad.creativeId
        headline = ad.title
        body = ad.description
        advertiser = ad.advertiser
        callToAction = ad.callToAction
        iconUri = ad.iconImageUrl?.let { Uri.parse(it) }
        mediaImageUri = ad.mainImageUrl?.let { Uri.parse(it) }

        val mediaHeight = ad.mainImageHeight
        if (mediaHeight > 0) {
            mediaContentAspectRatio = ad.mainImageWidth.toFloat() / mediaHeight
        }
        hasVideoContent = ad.creativeType == CreativeType.VIDEO
        hasAdChoices = true

        ad.setAdListener(this)
    }

    override fun createAdChoicesContentView(context: Context): View? {
        return AdChoiceView(context)
    }

    override fun createMediaContentView(context: Context): View? {
        return AdMediaView(context)
    }

    override val isExpired: Boolean
        get() = super.isExpired || ad.hasExpired()

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {

        val mediaView = assets.mediaView?.getChildAt(0) as? AdMediaView
        val adChoicesView = assets.adChoicesView?.getChildAt(0) as? AdChoiceView
        val builder = NativeAdViewBinder.Builder(container)
            .setAssetBindingMode(NativeAdViewBinder.AssetBindingMode.MANUAL)
            .setMediaView(mediaView)
            .setAdChoiceView(adChoicesView)
            .setTitleView(assets.headlineView)
            .setDescriptionView(assets.bodyView)
            .setCallToActionView(assets.callToActionView)
            .setDomainView(assets.advertiserView)
            .setIconView(assets.iconView)
            .setAdLabelView(assets.adLabelView)

        ad.registerViewForInteraction(builder.build())
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)

        ad.closeAd()
    }

    override fun destroy() {
        super.destroy()
        ad.destroy()
    }

    override fun onAdError(error: AdError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdDisplayed() {
        listener?.onAdImpression(this)
    }

    override fun onAdClicked() {
        listener?.onAdClicked(this)
    }

    override fun onAdOpened() {

    }

    override fun onAdClosed() {

    }
}