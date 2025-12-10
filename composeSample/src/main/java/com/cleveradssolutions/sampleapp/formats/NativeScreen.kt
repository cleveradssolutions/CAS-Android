package com.cleveradssolutions.sampleapp.formats

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASChoicesView
import com.cleveradssolutions.sdk.nativead.CASMediaView
import com.cleveradssolutions.sdk.nativead.CASNativeLoader
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.CASStarRatingView
import com.cleveradssolutions.sdk.nativead.NativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback
import com.cleversolutions.ads.AdError

@Composable
fun NativeScreen() {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAdContent?>(null) }
    val nativeView = remember { createCasNativeView(context) }
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        // Load the native ad when we launch this screen.
        loadNativeAd(
            context = context,
            onAdLoaded = { ad ->
                // Handle the native ad being loaded.
                if (!isDisposed) {
                    nativeAd = ad
                    nativeView.setNativeAd(ad)
                } else {
                    // Destroy the native ad if loaded after the screen is disposed.
                    ad.destroy()
                }
            },
        )
        // Destroy the native ad to prevent memory leaks when we dispose of this screen.
        onDispose {
            isDisposed = true
            nativeAd?.destroy()
            nativeAd = null
        }
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { nativeView }
        )
    }
}

private fun loadNativeAd(
    context: Context,
    onAdLoaded: (NativeAdContent) -> Unit
) {
    val loader =
        CASNativeLoader(
            context,
            CAS_ID,
            object : NativeAdContentCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
                    Log.d(TAG, "CAS Native ad was loaded.")
                    onAdLoaded(nativeAd)
                }

                override fun onNativeAdFailedToLoad(error: AdError) {
                    Log.e(TAG, "CAS Native ad failed to load: ${error.message}")
                }

                override fun onNativeAdFailedToShow(nativeAd: NativeAdContent, error: AdError) {
                    Log.e(TAG, "CAS Native ad failed to show: ${error.message}")
                }

                override fun onNativeAdClicked(nativeAd: NativeAdContent, ad: AdContentInfo) {
                    Log.d(TAG, "CAS Native ad was clicked.")
                }
            }
        ).apply {
            adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
            isStartVideoMuted = true
        }

    loader.load()
}


private fun createCasNativeView(context: Context): CASNativeView {
    val root =
        CASNativeView(context).apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    val container =
        LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }
    root.addView(container)

    val topRow =
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    val iconView =
        ImageView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            setPadding(4, 4, 4, 4)
        }

    val headlineView =
        TextView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            setPadding(4, 4, 4, 4)
        }

    val choicesView =
        CASChoicesView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

    topRow.addView(iconView)
    topRow.addView(headlineView)
    topRow.addView(choicesView)
    container.addView(topRow)

    val mediaView =
        CASMediaView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }
    container.addView(mediaView)

    val starRatingView =
        CASStarRatingView(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(
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

    return root
}

@Preview
@Composable
private fun NativeScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) { NativeScreen() }
    }
}
