package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
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
import com.cleveradssolutions.sdk.nativead.*
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize

/**
 * [START cas_template_screen]
 * Displays a built-in CAS native ad template using CASNativeView.
 *
 * This example demonstrates:
 *  • Creating a template-sized CASNativeView
 *  • Loading and binding a template-compatible native ad
 *  • Rendering the native view inside a Compose UI hierarchy
 */

@Composable
@SuppressLint("ConfigurationScreenWidthHeight")
fun NativeTemplateScreen() {

    val context = LocalContext.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    // CASNativeView should be created once per configuration.
    val nativeView = remember(screenWidthDp) {
        createTemplateNativeView(context, screenWidthDp)
    }

    var nativeAd by remember { mutableStateOf<NativeAdContent?>(null) }
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(nativeView) {

        loadTemplateNativeAd(
            context = context,
            onAdLoaded = { ad ->
                if (!isDisposed) {
                    nativeAd = ad
                    nativeView.setNativeAd(ad)
                } else ad.destroy()
            }
        )

        onDispose {
            isDisposed = true
            nativeAd?.destroy()
            nativeAd = null
        }
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
 *
 * The width equals device width, while height is fixed to 300dp.
 */
private fun createTemplateNativeView(
    context: Context,
    widthDp: Int,
): CASNativeView {

    return CASNativeView(context).apply {

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Inline template size
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
 * Renders a fully configured CASNativeView inside Compose.
 */
@Composable
private fun DisplayCasNativeTemplateView(nativeView: CASNativeView) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
