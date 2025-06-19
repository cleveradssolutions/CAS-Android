package com.cleveradssolutions.sampleapp.native

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity
import com.cleveradssolutions.sampleapp.util.toast
import com.cleveradssolutions.sampleapp.util.toastError
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASNativeLoader
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.NativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize

class NativeAdTemplateActivity : BackNavigationActivity() {

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
        setTitle(R.string.adFormats_nativeAdTemplate)

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

    private fun inflateNativeAdView(container: ViewGroup) {
        val adView = CASNativeView(this)
        this.adView = adView
        val size = AdSize.MEDIUM_RECTANGLE
        adView.setAdTemplateSize(size)

        customizeAdViewAppearance(adView)

        container.addView(adView)
    }

    private fun customizeAdViewAppearance(adView: CASNativeView) {
        // Default values are shown below:
        adView.setBackgroundColor(Color.WHITE)

        adView.callToActionView?.setBackgroundResource(com.cleveradssolutions.sdk.android.R.drawable.cas_rounded_button)
        adView.headlineView?.setTextColor(Color.parseColor("#80000000"))
    }

}