package com.cleversolutions.ads.android.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import kotlinx.android.synthetic.main.activity_sample_app_open_ad.*
import java.util.*
import java.util.concurrent.TimeUnit

class SampleAppOpenAd : Activity() {
    private var loadingAppResInProgress = false
    private var appOpenAdVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add <activity ... android:configChanges="orientation ..." /> in manifest to avoid calling onCreate() multiple times
        setContentView(R.layout.activity_sample_app_open_ad)

        simulationLongAppResourcesLoading()

        createAppOpenAd()
    }

    @SuppressLint("SetTextI18n")
    private fun createAppOpenAd() {
        // Try get last initialized MediationManager
        val initializedManager = CAS.manager

        // Create an Ad
        val appOpenAd = if (initializedManager == null)
            CASAppOpen.create("demo") // Replace demo to own identifier of MediationManager
        else
            CASAppOpen.create(initializedManager)

        // Handle fullscreen callback events
        appOpenAd.contentCallback = object : AdCallback {
            override fun onShown(ad: AdStatusHandler) {
                Log.d(SampleActivity.TAG, "App Open Ad shown")
                appOpenAdStatusView.text = "Show AppOpenAd"
            }

            override fun onShowFailed(message: String) {
                Log.d(SampleActivity.TAG, "App Open Ad show failed: $message")
                appOpenAdStatusView.text = "Show AppOpenAd failed: $message"
                appOpenAdVisible = false
                startNextActivity()
            }

            override fun onClosed() {
                Log.d(SampleActivity.TAG, "App Open Ad closed")
                appOpenAdStatusView.text = "AppOpenAd closed"
                appOpenAdVisible = false
                startNextActivity()
            }
        }

        // Load the Ad
        appOpenAdStatusView.text = "Loading AppOpenAd..."
        appOpenAd.loadAd(
            this,
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE,
            object : LoadAdCallback {
                override fun onAdFailedToLoad(error: AdError) {
                    Log.e(SampleActivity.TAG, "App Open Ad failed to load: ${error.message}")
                    appOpenAdStatusView.text = "Load AppOpenAd failed: ${error.message}"
                    startNextActivity()
                }

                override fun onAdLoaded() {
                    Log.d(SampleActivity.TAG, "App Open Ad loaded")
                    appOpenAdStatusView.text = "AppOpenAd loaded"
                    appOpenAdVisible = true
                    appOpenAd.show(this@SampleAppOpenAd)
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
        // Simulation of long application resources loading for 10 seconds.
        loadingAppResInProgress = true
        val timerText = timerView
        val timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(10), 1000) {
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