package com.cleversolutions.ads.android.sampleapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.AnyThread
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CASBannerView
import kotlinx.android.synthetic.main.activity_main.*

class SampleActivity : Activity(), AdLoadCallback {
    companion object {
        const val TAG = "CAS Sample"
    }

    private var statusAdViews: Map<AdType, TextView> = emptyMap()
    private var isActiveAppReturn: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusAdViews = mapOf(
            AdType.Banner to bannerStatusText,
            AdType.Interstitial to interStatusText,
            AdType.Rewarded to rewardedStatusText
        )

        // Try get last initialized MediationManager
        val manager = CAS.manager!!

        // Check ad available
        statusAdViews.forEach {
            it.value.text = if (manager.isAdReady(it.key)) "Ready" else "Loading"
        }

        // Validate Integration
        CAS.validateIntegration(this)

        // Get current SDK version
        casVersionText.text = "version: " + CAS.getSDKVersion()

        // Subscribe loading ad event
        manager.onAdLoadEvent.add(this)

        // Create Banner View
        val bannerView = CASBannerView(this)
        bannerView.listener = AdContentListener(this, AdType.Banner)
        bannerView.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

        // Attach banner View
        addContentView(
            bannerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        setDefaultSizeBtn.setOnClickListener {
            bannerView.size = AdSize.BANNER
        }

        setAdaptiveSizeBtn.setOnClickListener {
            bannerView.size = AdSize.getAdaptiveBannerInScreen(this)
        }

        setLedearboardSizeBtn.setOnClickListener {
            bannerView.size = AdSize.LEADERBOARD
        }

        setMrecSizeBtn.setOnClickListener {
            bannerView.size = AdSize.MEDIUM_RECTANGLE
        }

        showBannerBtn.setOnClickListener {
            bannerView.visibility = View.VISIBLE
        }

        hideBannerBtn.setOnClickListener {
            bannerView.visibility = View.GONE
        }

        showInterBtn.setOnClickListener {
            CAS.manager!!.showInterstitial(this, AdContentListener(this, AdType.Interstitial))
        }

        showRewardedBtn.setOnClickListener {
            CAS.manager!!.showRewardedAd(this, AdContentListener(this, AdType.Rewarded))
        }

        enableAppReturn.setOnClickListener {
            changeStateOfReturnAds()
        }
    }

    @AnyThread
    @SuppressLint("SetTextI18n")
    override fun onAdLoaded(type: AdType) {
        runOnUiThread {
            Log.d(TAG, "$type Ad Loaded")
            statusAdViews[type]?.text = "Ready"
        }
    }

    @AnyThread
    override fun onAdFailedToLoad(type: AdType, error: String?) {
        runOnUiThread {
            Log.d(TAG, "$type Ad Failed to Load: $error")
            statusAdViews[type]?.text = error
        }
    }

    private fun changeStateOfReturnAds() {
        if (isActiveAppReturn) {
            appReturnStatusText.text = "Disabled"
            enableAppReturn.text = "Enable"
            CAS.manager!!.disableAppReturnAds()
            isActiveAppReturn = false
        } else {
            appReturnStatusText.text = "Enabled"
            enableAppReturn.text = "Disable"
            CAS.manager!!.enableAppReturnAds(AdContentListener(this, AdType.Interstitial))
            isActiveAppReturn = true
        }
    }
}
