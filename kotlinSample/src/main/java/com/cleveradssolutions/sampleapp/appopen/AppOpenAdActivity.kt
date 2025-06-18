package com.cleveradssolutions.sampleapp.appopen

import android.os.Bundle
import android.widget.Button
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity
import com.cleveradssolutions.sampleapp.util.toast
import com.cleveradssolutions.sampleapp.util.toastError
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

        val appOpenAd = CASAppOpen(CAS_ID)

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