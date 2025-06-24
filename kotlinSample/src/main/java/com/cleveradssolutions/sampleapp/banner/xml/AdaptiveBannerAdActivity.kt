package com.cleveradssolutions.sampleapp.banner.xml

import android.os.Bundle
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView

class AdaptiveBannerAdActivity : BackNavigationActivity() {

    val bannerAdListener = object : AdViewListener {
        override fun onAdViewLoaded(view: CASBannerView) {
            toast("Banner Ad loaded")
        }

        override fun onAdViewFailed(view: CASBannerView, error: AdError) {
            toastError("Banner Ad failed to load: ${error.message}")
        }

        override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
            toast("Banner Ad shown")
        }

        override fun onAdViewClicked(view: CASBannerView) {
            toast("Banner Ad clicked")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adaptive_ad_activity)
        setTitle(R.string.adFormats_adaptiveBannerAd)

        val banner = findViewById<CASBannerView>(R.id.bannerView)
        banner.adListener = bannerAdListener
        banner.casId = CAS_ID
        banner.isAutoloadEnabled = true // by default

        // Optional set Impression listener
        banner.onImpressionListener = OnAdImpressionListener { ad ->
            toast("Banner Ad impression: ${ad.revenue}")
        }

        banner.load()
    }

}