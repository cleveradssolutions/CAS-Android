package com.cleveradssolutions.sampleapp.banner.programmatic

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView

class BannerAdActivity : BackNavigationActivity() {

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
        setContentView(R.layout.container_activity)
        setTitle(R.string.adFormats_bannerAd)

        val container = findViewById<FrameLayout>(R.id.container)

        val banner = CASBannerView(this, CAS_ID)
        banner.adListener = bannerAdListener
//        banner.size = AdSize.BANNER
//        banner.size = AdSize.MEDIUM_RECTANGLE
        banner.size = AdSize.getAdaptiveBannerInScreen(this)
        banner.isAutoloadEnabled = true // by default

        // Optional set Impression listener
        banner.onImpressionListener = OnAdImpressionListener { ad ->
            toast("Banner Ad impression: ${ad.revenue}")
        }

        container.addView(
            banner,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )

        banner.load()
    }

}