package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun NativeTemplateScreen() {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    Scaffold { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                factory = { ctx -> createTemplateNativeView(ctx, screenWidthDp) }
            )
        }
    }
}

private fun createTemplateNativeView(
    context: Context,
    widthDp: Int
): CASNativeView {
    val view =
        CASNativeView(context).apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            // Set inline template size using screen width and height 300dp.
            setAdTemplateSize(AdSize.getInlineBanner(widthDp, 300))
            setBackgroundColor(Color.WHITE)
        }

    val loader =
        CASNativeLoader(
            context,
            SampleApplication.CAS_ID,
            object : NativeAdContentCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
                    view.setNativeAd(nativeAd)
                }

                override fun onNativeAdFailedToLoad(error: AdError) {
                    // Optionally log error or show placeholder.
                }
            }
        ).apply {
            adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
            isStartVideoMuted = true
        }

    loader.load()

    return view
}

@Preview
@Composable
private fun NativeTemplateScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) { NativeTemplateScreen() }
    }
}
