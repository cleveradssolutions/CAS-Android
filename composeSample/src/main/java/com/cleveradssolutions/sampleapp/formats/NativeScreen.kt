package com.cleveradssolutions.sampleapp.formats

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.compose_util.*
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.*

@Composable
fun NativeScreen() {

    var adContent by remember { mutableStateOf<NativeAdContent?>(null) }
    val context = LocalContext.current
    var isDisposed by remember { mutableStateOf(false) }

    // Handle loading and cleanup of native ads.
    DisposableEffect(Unit) {

        val loader = CASNativeLoader(
            context,
            CAS_ID,
            object : NativeAdContentCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAdContent, info: AdContentInfo) {
                    Log.d(TAG, "Native ad loaded.")
                    if (!isDisposed) adContent = nativeAd else nativeAd.destroy()
                }

                override fun onNativeAdFailedToLoad(error: com.cleversolutions.ads.AdError) {
                    Log.e(TAG, "Native load failed: ${error.message}")
                }
            }
        )

        loader.load()

        onDispose {
            isDisposed = true
            adContent?.destroy()
            adContent = null
        }
    }

    // Render ad only after successful load.
    adContent?.let { DisplayNativeAd(it) }
}

/**
 * [END cas_load_native]
 */


/**
 * [START cas_native_template]
 * Renders a native ad using a user-defined Compose layout.
 *
 * Individual asset views (icon, headline, media, CTA, etc.)
 * are mapped to CASNativeView via wrappers from compose_util package.
 */

@Composable
fun DisplayNativeAd(ad: NativeAdContent) {
    NativeAdView(
        adContent = ad,
        modifier = Modifier.fillMaxWidth()
    ) { adView ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                NativeAdIconView(adView, Modifier.size(48.dp))
                Spacer(Modifier.width(8.dp))
                NativeAdHeadlineView(adView)
            }

            Spacer(Modifier.height(8.dp))
            NativeAdBodyView(adView)

            Spacer(Modifier.height(12.dp))
            NativeAdMediaView(
                adView,
                Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NativeAdPriceView(adView, Modifier.padding(end = 8.dp))
                NativeAdStoreView(adView, Modifier.padding(end = 12.dp))
                NativeAdCallToActionView(adView)
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                NativeAdStarRatingView(adView, Modifier.fillMaxWidth(0.8f))
            }
        }
    }
}


/*
 * [END cas_native_template]
 */
