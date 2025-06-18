package com.cleveradssolutions.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.selection.SelectionActivity
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.CASAppOpen
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError
import java.util.concurrent.TimeUnit

class SplashAppOpenAdActivity : Activity() {
    private var isLoadingAppResources = false
    private var isVisibleAppOpenAd = false
    private var isCompletedSplash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add <activity ... android:configChanges="orientation ..." />
        // in manifest to avoid calling onCreate() multiple times
        setContentView(R.layout.splash_app_open_ad_activity)

        simulationLongAppResourcesLoading()
        createAppOpenAd()
    }

    @SuppressLint("SetTextI18n")
    private fun createAppOpenAd() {
        // Create an Ad
        val appOpenAd = CASAppOpen(CAS_ID)

        // Handle fullscreen callback events
        appOpenAd.contentCallback = object : ScreenAdContentCallback() {
            override fun onAdLoaded(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad loaded")
                if (isLoadingAppResources) {
                    isVisibleAppOpenAd = true
                    appOpenAd.show(this@SplashAppOpenAdActivity)
                }
            }

            override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
                Log.e(TAG, "App Open Ad failed to load: ${error.message}")
                startNextActivity()
            }

            override fun onAdFailedToShow(format: AdFormat, error: AdError) {
                Log.e(TAG, "App Open Ad show failed: ${error.message}")
                isVisibleAppOpenAd = false
                startNextActivity()
            }

            override fun onAdShowed(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad shown")
            }

            override fun onAdClicked(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad clicked")
            }

            override fun onAdDismissed(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad closed")
                isVisibleAppOpenAd = false
                startNextActivity()
            }
        }

        appOpenAd.onImpressionListener = OnAdImpressionListener { ad ->
            Log.d(TAG, "App Open Ad impression: ${ad.revenue}")
        }

        appOpenAd.load(this)
    }

    private fun startNextActivity() {
        if (isLoadingAppResources || isVisibleAppOpenAd || isCompletedSplash)
            return
        isCompletedSplash = true
        val intent = Intent(this, SelectionActivity::class.java)
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