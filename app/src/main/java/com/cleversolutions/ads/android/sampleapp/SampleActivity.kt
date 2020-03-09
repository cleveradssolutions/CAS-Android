package com.cleversolutions.ads.android.sampleapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CASBannerView
import kotlinx.android.synthetic.main.activity_main.*

class SampleActivity : AppCompatActivity(), AdLoadCallback {
    companion object {
        const val TAG = "CAS Sample"
    }

    var userConsent = false
    var manager: MediationManager? = null
    var bannerView: CASBannerView? = null
    var statusAdViews: Map<AdType, TextView> = emptyMap()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusAdViews = mapOf(
            AdType.Banner to bannerStatusText,
            AdType.Interstitial to interStatusText,
            AdType.Rewarded to rewardedStatusText
        )

        statusAdViews.forEach {
            it.value.text = "Loading"
        }

        // Validate Integration in log
        CAS.validateIntegration(this, ContentRating.MA)

        // Get current SDK version
        casVersionText.text = "version: " + CAS.getSDKVersion()

        // Set Ads Settings
        CAS.settings.consent = userConsent
        CAS.settings.nativeDebug = true

        // Initialize SDK
        val manager = CAS.initialize(
            this,
            null, // Set CAS Application ID or null for select current bundle
            AdTypeFlags.Everything, // Set active Ad Types. 'AdTypeFlags.Banner or AdTypeFlags.Interstitial' for example.
            true // Demo ad fill only. Set FALSE for release!
        )
        this.manager = manager

        // Subscribe loading ad event
        manager.onAdLoadEvent.add(this)

        // Create Banner View
        val bannerView = CASBannerView(this, manager)
        this.bannerView = bannerView

        // Set banner settings
        bannerView.size = AdSize.Standard320x50
        // OR same
        // manager.bannerSize = AdSize.Standard320x50
        bannerView.position = AdPosition.BottomCenter
        bannerView.listener = AdListener(this, AdType.Banner)

        // Attach banner View
        addContentView(
            bannerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        setDefaultSizeBtn.setOnClickListener {
            manager.bannerSize = AdSize.Standard320x50
        }

        setAdaptiveSizeBtn.setOnClickListener {
            manager.bannerSize = AdSize.getAdaptiveBannerInScreen(this)
        }

        showBannerBtn.setOnClickListener {
            bannerView.visibility = View.VISIBLE
        }

        hideBannerBtn.setOnClickListener {
            bannerView.visibility = View.GONE
        }

        showInterBtn.setOnClickListener {
            manager.show(AdType.Interstitial, AdListener(this, AdType.Interstitial))
        }

        showRewardedBtn.setOnClickListener {
            manager.show(AdType.Rewarded, AdListener(this, AdType.Rewarded))
        }
    }

    override fun onAdFailedToLoad(type: AdType, error: String?) {
        runOnUiThread {
            Log.d(TAG, "$type Ad Failed to Load: $error")
            statusAdViews[type]?.text = error
        }
    }

    override fun onAdLoaded(type: AdType) {
        runOnUiThread {
            Log.d(TAG, "$type Ad Loaded")
            statusAdViews[type]?.text = "Ready"
        }
    }


}
