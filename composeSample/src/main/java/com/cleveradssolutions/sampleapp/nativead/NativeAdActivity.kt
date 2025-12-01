package com.cleveradssolutions.sampleapp.nativead

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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

class NativeAdActivity : BackNavigationActivity() {

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
        title = getString(R.string.adFormats_nativeAd)

        setContent {
            CASAndroidTheme {
                NativeAdScreen(
                    createAdView = { context -> createNativeAdView(context) }
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

    internal fun createNativeAdView(context: Context): CASNativeView {
        adView?.let { return it }

        val root = CASNativeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        root.addView(container)

        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val iconView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(4, 4, 4, 4)
        }

        val headlineView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            setPadding(4, 4, 4, 4)
        }

        val choicesView = CASChoicesView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        topRow.addView(iconView)
        topRow.addView(headlineView)
        topRow.addView(choicesView)
        container.addView(topRow)

        val mediaView = CASMediaView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        container.addView(mediaView)

        val starRatingView = CASStarRatingView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 4, 20, 4)
            }
        }
        container.addView(starRatingView)

        root.adChoicesView = choicesView
        root.mediaView = mediaView
        root.iconView = iconView
        root.headlineView = headlineView
        root.starRatingView = starRatingView

        adView = root
        return root
    }
}

@Composable
fun NativeAdScreen(
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
