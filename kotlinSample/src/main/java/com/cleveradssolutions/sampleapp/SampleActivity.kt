package com.cleveradssolutions.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CASBannerView


@SuppressLint("SetTextI18n")
class SampleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get current SDK version
        findViewById<TextView>(R.id.casVersionText).text = CAS.getSDKVersion()

        val adManager = SampleApplication.adManager
        createBanner(adManager)
        createInterstitial(adManager)
        createRewarded(adManager)

    }

    private fun createBanner(manager: MediationManager) {
        val container = findViewById<LinearLayout>(R.id.container)
        val bannerView = CASBannerView(this, manager)

        // Set required Ad size
        bannerView.size = AdSize.getAdaptiveBannerInScreen(this)
        //bannerView.size = AdSize.BANNER
        //bannerView.size = AdSize.LEADERBOARD
        //bannerView.size = AdSize.MEDIUM_RECTANGLE

        val label = findViewById<TextView>(R.id.bannerStatusText)
        // Set Ad content listener
        bannerView.adListener = object : AdViewListener {
            override fun onAdViewLoaded(view: CASBannerView) {
                label.text = "Loaded"
                Log.d(SampleApplication.TAG, "Banner Ad loaded and ready to present")
            }

            override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                label.text = error.message
                Log.e(SampleApplication.TAG, "Banner Ad received error: " + error.message)
            }

            override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
                label.text = "Presented: " + info.network
                Log.d(SampleApplication.TAG, "Banner Ad presented from " + info.network)
            }

            override fun onAdViewClicked(view: CASBannerView) {
                label.text = "Clicked"
                Log.d(SampleApplication.TAG, "Banner Ad received Click action")
            }
        }

        // Add view to container
        container.addView(bannerView)

        // Set controls
        findViewById<Button>(R.id.loadBannerBtn).setOnClickListener {
            label.text = "Loading"
            bannerView.loadNextAd()
        }

        findViewById<Button>(R.id.showBannerBtn).setOnClickListener {
            bannerView.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.hideBannerBtn).setOnClickListener {
            bannerView.visibility = View.GONE
        }
    }

    private fun createInterstitial(manager: MediationManager) {
        val label = findViewById<TextView>(R.id.interStatusText)

        // Set Ad load callback
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Interstitial) {
                    label.text = "Loaded"
                    Log.d(SampleApplication.TAG, "Interstitial Ad loaded and ready to show")
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Interstitial) {
                    label.text = error
                    Log.d(SampleApplication.TAG, "Interstitial Ad received error: $error")
                }
            }
        })

        // Create Ad content callback
        val contentCallback = object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                Log.d(SampleApplication.TAG, "Interstitial Ad shown from " + ad.network)
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                Log.d(SampleApplication.TAG, "Interstitial Ad revenue paid from " + ad.network)
            }

            override fun onShowFailed(message: String) {
                Log.e(SampleApplication.TAG, "Interstitial Ad show failed: $message")
                label.text = message
            }

            override fun onClicked() {
                Log.d(SampleApplication.TAG, "Interstitial Ad received Click")
            }

            override fun onClosed() {
                Log.d(SampleApplication.TAG, "Interstitial Ad received Close")
                label.text = "Closed"
            }
        }

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
            findViewById<Button>(R.id.loadInterBtn).setOnClickListener {
                label.text = "Loading"
                manager.loadInterstitial()
            }
        } else {
            label.text = "Loading"
            findViewById<Button>(R.id.loadInterBtn).visibility = View.GONE
        }

        findViewById<Button>(R.id.showInterBtn).setOnClickListener {
            if (manager.isInterstitialReady)
                manager.showInterstitial(this, contentCallback)
            else
                Log.e(SampleApplication.TAG, "Interstitial Ad not ready to show")
        }
    }

    private fun createRewarded(manager: MediationManager) {
        val label = findViewById<TextView>(R.id.rewardedStatusText)

        // Set Ad load callback
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Rewarded) {
                    label.text = "Loaded"
                    Log.d(SampleApplication.TAG, "Rewarded Ad loaded and ready to show")
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Rewarded) {
                    label.text = error
                    Log.d(SampleApplication.TAG, "Rewarded Ad received error: $error")
                }
            }
        })

        // Create Ad content callback
        val contentCallback = object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                Log.d(SampleApplication.TAG, "Rewarded Ad shown from " + ad.network)
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                Log.d(SampleApplication.TAG, "Rewarded Ad revenue paid from " + ad.network)
            }

            override fun onShowFailed(message: String) {
                Log.e(SampleApplication.TAG, "Rewarded Ad show failed: $message")
                label.text = message
            }

            override fun onClicked() {
                Log.d(SampleApplication.TAG, "Rewarded Ad received Click")
            }

            override fun onComplete() {
                val dialog = AlertDialog.Builder(this@SampleActivity).apply {
                    setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                    setTitle("Rewarded Ad complete")
                    setMessage("You have been rewarded")
                    setCancelable(false)
                    create()
                }
                dialog.show()
            }

            override fun onClosed() {
                Log.d(SampleApplication.TAG, "Rewarded Ad received Close")
                label.text = "Closed"
            }
        }

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
            findViewById<Button>(R.id.loadRewardedBtn).setOnClickListener {
                label.text = "Loading"
                manager.loadRewardedAd()
            }
        } else {
            label.text = "Loading"
            findViewById<Button>(R.id.loadRewardedBtn).visibility = View.GONE
        }

        findViewById<Button>(R.id.showRewardedBtn).setOnClickListener {
            if (manager.isRewardedAdReady)
                manager.showRewardedAd(this, contentCallback)
            else
                Log.e(SampleApplication.TAG, "Rewarded Ad not ready to show")
        }
    }
}
