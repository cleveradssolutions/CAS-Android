package com.cleveradssolutions.sampleapp.appopen

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
import com.cleveradssolutions.sdk.screen.CASAppOpen
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError

class AppOpenAdActivity : BackNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_ad_activity)
        setTitle(R.string.adFormats_appOpenAd)

        val appOpenAd = CASAppOpen(CAS_ID)
        appOpenAd.isAutoloadEnabled = false // by default
        appOpenAd.isAutoshowEnabled = false // by default

        appOpenAd.contentCallback = object : ScreenAdContentCallback() {
            override fun onAdLoaded(ad: AdContentInfo) {
                toast("App Open Ad loaded")
            }

            override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
                toastError("App Open Ad failed to load: ${error.message}")
            }

            override fun onAdFailedToShow(format: AdFormat, error: AdError) {
                toastError("App Open Ad show failed: ${error.message}")
            }

            override fun onAdShowed(ad: AdContentInfo) {
                toast("App Open Ad shown")
            }

            override fun onAdClicked(ad: AdContentInfo) {
                toast("App Open Ad clicked")
            }

            override fun onAdDismissed(ad: AdContentInfo) {
                toast("App Open Ad closed")
            }
        }

        // Optional set Impression listener
        appOpenAd.onImpressionListener = OnAdImpressionListener { ad ->
            toast("App Open Ad impression: ${ad.revenue}")
        }

        findViewById<Button>(R.id.load).setOnClickListener {
            appOpenAd.load(this)
        }

        findViewById<Button>(R.id.show).setOnClickListener {
            appOpenAd.show(this)
        }
    }

}