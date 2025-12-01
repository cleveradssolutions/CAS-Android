package com.cleveradssolutions.sampleapp.banner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView

class MRECBannerAdActivity : BackNavigationActivity() {

    private var bannerView: CASBannerView? = null

    private val bannerAdListener = object : AdViewListener {
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

        title = getString(R.string.adFormats_mrecBannerAd)

        setContent {
            CASAndroidTheme {
                MrecBannerAdScreen(activity = this)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerView?.destroy()
        bannerView = null
    }

    internal fun createMrecBannerView(): CASBannerView {
        val view = CASBannerView(this, CAS_ID).apply {
            size = AdSize.MEDIUM_RECTANGLE
            adListener = bannerAdListener
            isAutoloadEnabled = true

            onImpressionListener = OnAdImpressionListener { ad ->
                toast("Banner Ad impression: ${ad.revenue}")
            }

            load()
        }
        bannerView = view
        return view
    }
}

@Composable
private fun MrecBannerAdScreen(
    activity: MRECBannerAdActivity
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier,
                factory = { activity.createMrecBannerView() }
            )
        }
    }
}
