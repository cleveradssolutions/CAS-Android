package com.cleveradssolutions.sampleapp.nativead

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASNativeLoader
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.NativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback
import com.cleversolutions.ads.AdError


class NativeAdActivity : BackNavigationActivity() {

    private var adView: CASNativeView? = null

    private val nativeAdCallback: NativeAdContentCallback = object : NativeAdContentCallback() {
        override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
            toast("Native Ad loaded")
            val container = findViewById<FrameLayout>(R.id.container)
            inflateNativeAdView(container)
            registerNativeAdContent(nativeAd)
        }

        override fun onNativeAdFailedToLoad(error: AdError) {
            toastError("Native Ad failed to load: ${error.message}")
        }

        override fun onNativeAdFailedToShow(nativeAd: NativeAdContent, error: AdError) {
            toastError("Native Ad show failed: ${error.message}")
        }

        override fun onNativeAdClicked(nativeAd: NativeAdContent, ad: AdContentInfo) {
            toast("Native Ad clicked")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_activity)
        setTitle(R.string.adFormats_nativeAd)

        loadNativeAd()
    }

    private fun loadNativeAd() {
        val adLoader = CASNativeLoader(this, CAS_ID, nativeAdCallback)
        adLoader.adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
        adLoader.isStartVideoMuted = true

        adLoader.load()
    }

    private fun registerNativeAdContent(nativeAd: NativeAdContent) {
        adView?.setNativeAd(nativeAd)
    }

    private fun inflateNativeAdView(container: FrameLayout) {
        val adView = LayoutInflater.from(container.context)
            .inflate(R.layout.native_ad_layout, container, false) as CASNativeView
        container.addView(adView)
        registerAdAssetViews(adView)
        this.adView = adView
    }

    private fun registerAdAssetViews(adView: CASNativeView) {
        // You can also omit adChoicesView and it will be created automatically.
        adView.adChoicesView = adView.findViewById(R.id.ad_choices_view)

        adView.mediaView = adView.findViewById(R.id.ad_media_view)
//        adView.adLabelView = adView.findViewById(R.id.ad_label)
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.iconView = adView.findViewById(R.id.ad_icon)
//        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
//        adView.bodyView = adView.findViewById(R.id.ad_body)
//        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
//        adView.storeView = adView.findViewById(R.id.ad_store)
//        adView.priceView = adView.findViewById(R.id.ad_price)
//        adView.reviewCountView = adView.findViewById(R.id.ad_review_count)
        adView.starRatingView = adView.findViewById(R.id.ad_star_rating)
    }

}