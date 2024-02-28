package com.cleveradssolutions.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleversolutions.ads.*
import java.util.*
import java.util.concurrent.TimeUnit

class SampleAppOpenAdActivity : Activity() {
    private var isLoadingAppResources = false
    private var isVisibleAppOpenAd = false
    private var isCompletedSplash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add <activity ... android:configChanges="orientation ..." />
        // in manifest to avoid calling onCreate() multiple times
        setContentView(R.layout.activity_sample_app_open_ad)

        simulationLongAppResourcesLoading()
        createAppOpenAd()
    }

    @SuppressLint("SetTextI18n")
    private fun createAppOpenAd() {
        // Create an Ad
        val appOpenAd = CASAppOpen.create(CAS_ID)

        // Handle fullscreen callback events
        appOpenAd.contentCallback = object : AdCallback {
            override fun onShown(ad: AdStatusHandler) {
                Log.d(TAG, "App Open Ad shown")
            }

            override fun onShowFailed(message: String) {
                Log.e(TAG, "App Open Ad show failed: $message")
                isVisibleAppOpenAd = false
                startNextActivity()
            }

            override fun onClicked() {
                Log.d(TAG, "App Open Ad clicked")
            }

            override fun onClosed() {
                Log.d(TAG, "App Open Ad closed")
                isVisibleAppOpenAd = false
                startNextActivity()
            }
        }

        // Load the Ad
        appOpenAd.loadAd(
            this,
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
            object : LoadAdCallback {
                override fun onAdLoaded() {
                    Log.d(TAG, "App Open Ad loaded")
                    if (isLoadingAppResources) {
                        isVisibleAppOpenAd = true
                        appOpenAd.show(this@SampleAppOpenAdActivity)
                    }
                }

                override fun onAdFailedToLoad(error: AdError) {
                    Log.e(TAG, "App Open Ad failed to load: ${error.message}")
                    startNextActivity()
                }
            })
    }

    private fun startNextActivity() {
        if (isLoadingAppResources || isVisibleAppOpenAd || isCompletedSplash)
            return
        isCompletedSplash = true
        val intent = Intent(this, SampleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun simulationLongAppResourcesLoading() {
        // Simulation of long application resources loading for 5 seconds.
        isLoadingAppResources = true
        val timerText = findViewById<TextView>(R.id.timerView)
        val timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(7), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timerText.text =
                    "${TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)} seconds left"
            }

            override fun onFinish() {
                isLoadingAppResources = false
                startNextActivity()
            }
        }
        timer.start()

        findViewById<View>(R.id.skipAppOpenAd).setOnClickListener { v: View? ->
            isLoadingAppResources = false
            startNextActivity()
        }
    }
}