package com.cleversolutions.ads.android.sampleapp

import android.app.Application
import android.util.Log
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS

class SampleApplication : Application() {

    companion object {
        const val TAG = "CAS Sample"
        lateinit var adManager: MediationManager
    }

    override fun onCreate() {
        super.onCreate()

        // Set Ads Settings
        CAS.settings.debugMode = true
        CAS.settings.taggedAudience = Audience.NOT_CHILDREN

        // Set Manual loading mode to disable auto requests
        //CAS.settings.loadingMode = LoadingManagerMode.Manual

        // Initialize SDK
        adManager = CAS.buildManager()
            .withManagerId("demo")
            .withTestAdMode(true)
            .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
            .withConsentFlow(
                ConsentFlow(isEnabled = true)
                //.withPrivacyPolicy("https://url")
            )
            .withCompletionListener {
                if (it.error == null)
                    Log.d(TAG, "Ad manager initialized")
                else
                    Log.d(TAG, "Ad manager initialization failed: " + it.error)
            }
            .initialize(this)
    }

}
