package com.cleveradssolutions.sampleapp.rewarded

import android.os.Bundle
import android.widget.Button
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.CASRewarded
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError

class RewardedAdActivity : BackNavigationActivity() {

    val screenContentCallback = object : ScreenAdContentCallback() {
        override fun onAdLoaded(ad: AdContentInfo) {
            toast("Rewarded Ad loaded")
        }

        override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
            toastError("Rewarded Ad failed to load: ${error.message}")
        }

        override fun onAdFailedToShow(format: AdFormat, error: AdError) {
            toastError("Rewarded Ad show failed: ${error.message}")
        }

        override fun onAdShowed(ad: AdContentInfo) {
            toast("Rewarded Ad shown")
        }

        override fun onAdClicked(ad: AdContentInfo) {
            toast("Rewarded Ad clicked")
        }

        override fun onAdDismissed(ad: AdContentInfo) {
            toast("Rewarded Ad closed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_ad_activity)
        setTitle(R.string.adFormats_rewardedAd)

        val rewardedAd = CASRewarded(CAS_ID)
        rewardedAd.contentCallback = screenContentCallback
        rewardedAd.isAutoloadEnabled = false // by default

        // Optional set Impression listener
        rewardedAd.onImpressionListener = OnAdImpressionListener { ad ->
            toast("Rewarded Ad impression: ${ad.revenue}")
        }

        findViewById<Button>(R.id.load).setOnClickListener {
            rewardedAd.load(this)
        }

        findViewById<Button>(R.id.show).setOnClickListener {
            rewardedAd.show(this) {
                toast("Reward earned")
            }
        }
    }

}