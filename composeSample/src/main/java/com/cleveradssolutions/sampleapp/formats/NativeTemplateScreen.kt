package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.SampleApplication
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASNativeLoader
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.NativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize
import kotlin.math.max

/**
 * [START cas_template_screen]
 * Displays a built-in CAS native ad template using CASNativeView.
 *
 * This example demonstrates:
 *  • Creating a template-sized CASNativeView
 *  • Loading a native ad suitable for template rendering
 *  • Rendering the native view inside a Compose hierarchy
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NativeTemplateScreen() {
    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    // We render the view with horizontal padding = 16dp, so template width must match that space.
    val availableWidthDp = remember(screenWidthDp) { max(0, screenWidthDp - 32) }

    // Create CASNativeView once per width change.
    val nativeView = remember(availableWidthDp) {
        createTemplateNativeView(context, availableWidthDp)
    }

    var adContent by remember { mutableStateOf<NativeAdContent?>(null) }
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        loadTemplateNativeAd(
            context = context,
            onAdLoaded = { ad ->
                if (!isDisposed) {
                    // If you ever reload, destroy previous instance.
                    adContent?.destroy()
                    adContent = ad
                } else {
                    ad.destroy()
                }
            }
        )

        onDispose {
            isDisposed = true
            adContent?.destroy()
            adContent = null
        }
    }

    // Apply ad to view whenever adContent OR nativeView changes.
    LaunchedEffect(adContent, nativeView) {
        nativeView.setNativeAd(adContent)
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            DisplayCasNativeTemplateView(nativeView)
        }
    }
}
/**
 * [END cas_template_screen]
 */

/**
 * [START cas_template_view]
 * Creates a CASNativeView preconfigured for template rendering.
 */
private fun createTemplateNativeView(
    context: Context,
    widthDp: Int,
): CASNativeView {
    return CASNativeView(context).apply {
        // Keep ViewGroup params as MATCH_PARENT. Control size in Compose using modifiers.
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Configure template size (width = available width, height = 300dp).
        setAdTemplateSize(AdSize.getInlineBanner(widthDp, 300))
        setBackgroundColor(Color.WHITE)
    }
}
/**
 * [END cas_template_view]
 */

/**
 * [START cas_template_load]
 * Loads a native ad suitable for template rendering.
 */
private fun loadTemplateNativeAd(
    context: Context,
    onAdLoaded: (NativeAdContent) -> Unit,
) {
    val callback = object : NativeAdContentCallback() {
        override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
            onAdLoaded(nativeAd)
        }

        override fun onNativeAdFailedToLoad(error: AdError) {
            // Optional: provide fallback UI
        }
    }

    CASNativeLoader(context, SampleApplication.CAS_ID, callback).apply {
        adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
        isStartVideoMuted = true
        load()
    }
}
/**
 * [END cas_template_load]
 */

/**
 * [START cas_template_display]
 * Renders a configured CASNativeView inside Compose.
 */
@Composable
private fun DisplayCasNativeTemplateView(nativeView: CASNativeView) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp),
        factory = { nativeView }
    )
}
/**
 * [END cas_template_display]
 */

@Preview
@Composable
private fun NativeTemplateScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            NativeTemplateScreen()
        }
    }
}
