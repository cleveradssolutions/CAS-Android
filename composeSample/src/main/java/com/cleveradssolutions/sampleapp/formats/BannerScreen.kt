package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.SampleApplication
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BannerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    // Create CAS banner view instance.
    val bannerView = remember(screenWidthDp) {
        val view = CASBannerView(context, SampleApplication.CAS_ID)
        view.isAutoloadEnabled = true // by default

        // Set the adaptive banner ad size with a given width.
        view.size = AdSize.getAdaptiveBanner(context, screenWidthDp)

        // [Optional] Set an AdViewListener to receive callbacks for various ad events.
        view.adListener =
            object : AdViewListener {
                override fun onAdViewLoaded(view: CASBannerView) {
                    Log.d(TAG, "CAS Banner ad was loaded.")
                }

                override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                    Log.e(TAG, "CAS Banner ad failed to load: ${error.message}")
                }

                override fun onAdViewClicked(view: CASBannerView) {
                    Log.d(TAG, "CAS Banner ad was clicked.")
                }
            }

        // Impression-level callback from CAS SDK.
        view.onImpressionListener = OnAdImpressionListener { ad ->
            Log.d(TAG, "CAS Banner impression = ${ad.impressionDepth}")
        }

        view
    }

    // Load / destroy banner with a DisposableEffect.
    DisposableEffect(bannerView, isPreview) {
        // Prevent loading the AdView if the app is in preview mode.
        if (!isPreview) {
            // Load the banner ad.
            bannerView.load()
        }
        // Destroy the AdView to prevent memory leaks when the screen is disposed.
        onDispose { bannerView.destroy() }
    }

    // Place the banner at the bottom of the screen.
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { bannerView },
            )
        }
    }
}

@Preview
@Composable
private fun BannerScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) { BannerScreen() }
    }
}
