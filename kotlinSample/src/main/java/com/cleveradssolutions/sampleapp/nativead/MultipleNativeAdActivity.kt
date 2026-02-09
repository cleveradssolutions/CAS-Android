package com.cleveradssolutions.sampleapp.nativead

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
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
import com.cleversolutions.ads.AdSize
import java.util.LinkedList
import java.util.Queue

class MultipleNativeAdActivity : BackNavigationActivity() {

    private var adView: CASNativeView? = null
    private val loadedNativeAds: Queue<NativeAdContent> = LinkedList()

    private val nativeAdCallback: NativeAdContentCallback = object : NativeAdContentCallback() {
        override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
            keepNativeAdInMemory(nativeAd)
            toast("Native Ad loaded: ${loadedNativeAds.size} in total")
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
        setContentView(R.layout.multiple_native_ad_activity)
        setTitle(R.string.adFormats_multipleNativeAd)

        findViewById<Button>(R.id.load).setOnClickListener {
            loadNativeAd()
        }

        findViewById<Button>(R.id.show).run {
            setOnClickListener {
                showNextNativeAdContent()
            }
        }
    }

    override fun onDestroy() {
        for (ad in loadedNativeAds) {
            ad.destroy()
        }
        super.onDestroy()
    }

    private fun loadNativeAd() {
        val adLoader = CASNativeLoader(this, CAS_ID, nativeAdCallback)
        adLoader.adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
        adLoader.isStartVideoMuted = true

        val maxNumberOfAdsToLoad = 3
        adLoader.load(maxNumberOfAdsToLoad)
    }

    private fun keepNativeAdInMemory(nativeAd: NativeAdContent) {
        loadedNativeAds.add(nativeAd)
    }

    private fun showNextNativeAdContent() {
        val nativeAd = loadedNativeAds.poll()
        if (nativeAd != null) {
            if (!nativeAd.isExpired) {
                getAdView().bindAdContent(nativeAd)
                toast("Native Ad shown: ${loadedNativeAds.size} left")
            } else {
                showNextNativeAdContent()
            }
        } else {
            toastError("Native Ad not loaded")
        }
    }

    private fun getAdView(): CASNativeView {
        return adView ?: inflateNativeAdView(findViewById<FrameLayout>(R.id.container))
    }

    private fun inflateNativeAdView(container: ViewGroup): CASNativeView {
        val adView = CASNativeView(this)
        this.adView = adView
        val size = AdSize.MEDIUM_RECTANGLE
        adView.setAdTemplateSize(size)

        container.addView(adView)

        return adView
    }

}