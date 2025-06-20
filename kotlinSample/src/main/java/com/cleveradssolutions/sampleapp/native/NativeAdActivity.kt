package com.cleveradssolutions.sampleapp.native

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity
import com.cleveradssolutions.sampleapp.util.toast
import com.cleveradssolutions.sampleapp.util.toastError
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASChoicesView
import com.cleveradssolutions.sdk.nativead.CASMediaView
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
        adView.adChoicesView = adView.findViewById<View>(R.id.ad_choices_view) as CASChoicesView

        adView.mediaView = adView.findViewById<View>(R.id.ad_media_view) as CASMediaView
//        adView.adLabelView = adView.findViewById<View>(R.id.ad_label) as TextView
        adView.headlineView = adView.findViewById<View>(R.id.ad_headline) as TextView
        adView.iconView = adView.findViewById<View>(R.id.ad_icon) as ImageView
//        adView.callToActionView = adView.findViewById<View>(R.id.ad_call_to_action) as Button
//        adView.bodyView = adView.findViewById<View>(R.id.ad_body) as TextView
//        adView.advertiserView = adView.findViewById<View>(R.id.ad_advertiser) as TextView
//        adView.storeView = adView.findViewById<View>(R.id.ad_store) as TextView
//        adView.priceView = adView.findViewById<View>(R.id.ad_price) as TextView
//        adView.reviewCountView = adView.findViewById<View>(R.id.ad_review_count) as TextView
        adView.starRatingView = adView.findViewById<View>(R.id.ad_star_rating)
    }

}