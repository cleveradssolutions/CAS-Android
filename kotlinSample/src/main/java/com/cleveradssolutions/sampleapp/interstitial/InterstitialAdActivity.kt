package com.cleveradssolutions.sampleapp.interstitial

import android.os.Bundle
import android.widget.Button
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.CASInterstitial
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError

class InterstitialAdActivity : BackNavigationActivity() {

    val screenContentCallback = object : ScreenAdContentCallback() {
        override fun onAdLoaded(ad: AdContentInfo) {
            toast("Interstitial Ad loaded")
        }

        override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
            toastError("Interstitial Ad failed to load: ${error.message}")
        }

        override fun onAdFailedToShow(format: AdFormat, error: AdError) {
            toastError("Interstitial Ad show failed: ${error.message}")
        }

        override fun onAdShowed(ad: AdContentInfo) {
            toast("Interstitial Ad shown")
        }

        override fun onAdClicked(ad: AdContentInfo) {
            toast("Interstitial Ad clicked")
        }

        override fun onAdDismissed(ad: AdContentInfo) {
            toast("Interstitial Ad closed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_ad_activity)
        setTitle(R.string.adFormats_interstitialAd)

        val interstitialAd = CASInterstitial(CAS_ID)
        interstitialAd.contentCallback = screenContentCallback
        interstitialAd.isAutoloadEnabled = false // by default
        interstitialAd.isAutoshowEnabled = false // by default

        // Optional set Impression listener
        interstitialAd.onImpressionListener = OnAdImpressionListener { ad ->
            toast("Interstitial Ad impression: ${ad.revenue}")
        }

        findViewById<Button>(R.id.load).setOnClickListener {
            interstitialAd.load(this)
        }

        findViewById<Button>(R.id.show).setOnClickListener {
            interstitialAd.show(this)
        }
    }

}