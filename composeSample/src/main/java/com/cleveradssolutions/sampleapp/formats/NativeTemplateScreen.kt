package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement
import com.cleveradssolutions.sdk.nativead.CASNativeLoader
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.NativeAdContent
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize

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
    val isPreview = LocalInspectionMode.current
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    // Create CASNativeView once per width change.
    val adView = remember(screenWidthDp) {
        CASNativeView(context).apply {
            // Set template layout size
            setAdTemplateSize(
                AdSize.getInlineBanner(screenWidthDp, 300)
            )
            // Set view layout params to match parent width
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Reset default white background to use compose modifier
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            // Optional.
            // Customization of ad assets using standard Android view functions.
            //headlineView?.textSize
            //bodyView?.setTextColor()
            //callToActionView?.setBackgroundColor()
            // and for other asset views.
        }
    }

    var adContent by remember { mutableStateOf<NativeAdContent?>(null) }
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val adCallback = object : NativeAdContentCallback() {
            override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
                if (isDisposed) {
                    nativeAd.destroy()
                } else {
                    // If you ever reload, destroy previous instance.
                    adContent?.destroy()
                    adContent = nativeAd
                }
            }

            override fun onNativeAdFailedToLoad(error: AdError) {
                // Optional: provide fallback UI
            }
        }

        CASNativeLoader(context, CAS_ID, adCallback).apply {
            adChoicesPlacement = AdChoicesPlacement.TOP_RIGHT
            isStartVideoMuted = true
            if (!isPreview) {
                load()
            }
        }

        onDispose {
            isDisposed = true
            adContent?.destroy()
            adContent = null
        }
    }

    // Apply ad to view whenever adContent OR nativeView changes.
    LaunchedEffect(adContent, adView) {
        adView.setNativeAd(adContent)
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.Gray)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .background(Color.White)
                    .padding(5.dp),
                factory = { adView }
            )
        }

    }
}

/**
 * [END cas_template_screen]
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
