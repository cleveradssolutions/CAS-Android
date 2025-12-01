package com.cleveradssolutions.sampleapp.nativead

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.*
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize

class NativeAdTemplateActivity : BackNavigationActivity() {

    private var adView: CASNativeView? = null

    private val nativeAdCallback = object : NativeAdContentCallback() {
        override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
            toast("Native Ad loaded")
            adView?.setNativeAd(nativeAd)
        }

        override fun onNativeAdFailedToLoad(error: AdError) {
            toastError("Native Ad failed to load: ${error.message}")
        }

        override fun onNativeAdFailedToShow(nativeAd: NativeAdContent, error: AdError) {
            toastError("Native Ad show failed: ${error.message}")
        }

        override fun onNativeAdClicked(nativeAd: NativeAdContent, ad: AdContentInfo) {
            toast("Native Ad clicked")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.adFormats_nativeAdTemplate)

        setContent {
            CASAndroidTheme {
                NativeAdTemplateScreen(
                    createAdView = { context -> createTemplateNativeView(context) }
                )
            }
        }

        loadNativeAd()
    }

    private fun loadNativeAd() {
        val adLoader = CASNativeLoader(this, CAS_ID, nativeAdCallback).apply {
            adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
            isStartVideoMuted = true
        }
        adLoader.load()
    }

    internal fun createTemplateNativeView(context: Context): CASNativeView {
        adView?.let { return it }

        val view = CASNativeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setAdTemplateSize(AdSize.MEDIUM_RECTANGLE)
            setBackgroundColor(Color.WHITE)
        }

        adView = view
        return view
    }
}

@Composable
fun NativeAdTemplateScreen(
    createAdView: (Context) -> CASNativeView
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                factory = { context -> createAdView(context) }
            )
        }
    }
}
