package com.cleversolutions.ads.android.sampleapp

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.AdTypeFlags
import com.cleversolutions.ads.OnInitializationListener
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.basement.CASAnalytics

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // (Optional) Apply Analytics receiver
        CASAnalytics.handler = object : CASAnalytics.Handler {
            override fun log(eventName: String, content: Bundle) {
                Log.i(SampleActivity.TAG, "Analytics log event $eventName:")
                for (key in content.keySet()) {
                    Log.i(SampleActivity.TAG, "\t $key:${content.get(key)}")
                }
            }
        }

        // Set Ads Settings
        CAS.settings.debugMode = true
        CAS.settings.analyticsCollectionEnabled = true
        CAS.settings.allowInterstitialAdsWhenVideoCostAreLower = true

        // Initialize SDK
        val manager = CAS.buildManager()
            .withManagerId("demo")
            .withEnabledAdTypes(AdTypeFlags.Banner or AdTypeFlags.Interstitial or AdTypeFlags.Rewarded)
            .withTestAdMode(true)
            .withInitListener(OnInitializationListener { success, error ->
                Log.i(SampleActivity.TAG, "CAS initialize success: $success with error: $error")
            })
            .initialize(this)

        // Set banner size
        manager.bannerSize = AdSize.BANNER
    }
}