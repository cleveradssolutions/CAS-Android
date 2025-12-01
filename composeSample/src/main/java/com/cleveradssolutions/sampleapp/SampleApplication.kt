package com.cleveradssolutions.sampleapp

import android.app.Application
import android.util.Log
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.android.CAS

class SampleApplication : Application() {

    companion object {
        const val TAG = "CAS Sample"
        const val CAS_ID = "demo"
    }

    override fun onCreate() {
        super.onCreate()

        CAS.buildManager()
            .withCasId(CAS_ID)
            .withTestAdMode(BuildConfig.DEBUG)
            .withConsentFlow(
                ConsentFlow(isEnabled = true)
                    .withDismissListener {
                        Log.d(TAG, "Consent flow dismissed")
                    }
            )
            .withCompletionListener {
                if (it.error == null) {
                    Log.d(TAG, "Ad manager initialized")
                } else {
                    Log.e(TAG, "Ad manager initialization failed: " + it.error)
                }
            }
            .build(this)
    }

}
