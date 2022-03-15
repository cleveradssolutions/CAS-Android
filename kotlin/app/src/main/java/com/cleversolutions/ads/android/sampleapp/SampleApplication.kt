package com.cleversolutions.ads.android.sampleapp

import android.app.Application
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.basement.CASAnalytics

class SampleApplication : Application() {

    companion object {
        var manager: MediationManager? = null
    }

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
        manager = CAS.buildManager()
            .withManagerId("demo")
            .withTestAdMode(true)
            .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
            .withInitListener(OnInitializationListener { success, error ->
                Log.i(SampleActivity.TAG, "CAS initialize success: $success with error: $error")
            })
            .initialize(this)
    }
}