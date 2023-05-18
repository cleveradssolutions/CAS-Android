package com.cleversolutions.ads.android.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import com.cleversolutions.ads.*
import java.util.*
import java.util.concurrent.TimeUnit

class SampleAppOpenAdActivity : Activity() {
    private var loadingAppResInProgress = false
    private var appOpenAdVisible = false

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
        // Try get last initialized MediationManager
        val adManager = SampleApplication.adManager

        // Create an Ad
        val appOpenAd = CASAppOpen.create(adManager)

        // Handle fullscreen callback events
        appOpenAd.contentCallback = object : AdCallback {
            override fun onShown(ad: AdStatusHandler) {
                Log.d(SampleApplication.TAG, "App Open Ad shown")
            }

            override fun onShowFailed(message: String) {
                Log.e(SampleApplication.TAG, "App Open Ad show failed: $message")
                appOpenAdVisible = false
                startNextActivity()
            }

            override fun onClicked() {
                Log.d(SampleApplication.TAG, "App Open Ad clicked")
            }

            override fun onClosed() {
                Log.d(SampleApplication.TAG, "App Open Ad closed")
                appOpenAdVisible = false
                startNextActivity()
            }
        }

        // Load the Ad
        appOpenAd.loadAd(
            this,
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
            object : LoadAdCallback {
                override fun onAdFailedToLoad(error: AdError) {
                    Log.e(SampleApplication.TAG, "App Open Ad failed to load: ${error.message}")
                    startNextActivity()
                }

                override fun onAdLoaded() {
                    Log.d(SampleApplication.TAG, "App Open Ad loaded")
                    appOpenAdVisible = true
                    appOpenAd.show(this@SampleAppOpenAdActivity)
                }
            })
    }

    private fun startNextActivity() {
        if (loadingAppResInProgress || appOpenAdVisible)
            return
        val intent = Intent(this, SampleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun simulationLongAppResourcesLoading() {
        // Simulation of long application resources loading for 5 seconds.
        loadingAppResInProgress = true
        val timerText = findViewById<TextView>(R.id.timerView)
        val timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(5), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                timerText.text =
                    "${TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)} seconds left"
            }

            override fun onFinish() {
                loadingAppResInProgress = false
                startNextActivity()
            }
        }
        timer.start()
    }
}