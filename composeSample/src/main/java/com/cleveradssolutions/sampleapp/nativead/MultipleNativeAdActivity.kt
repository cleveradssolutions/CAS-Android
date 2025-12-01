package com.cleveradssolutions.sampleapp.nativead

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import java.util.LinkedList
import java.util.Queue

class MultipleNativeAdActivity : BackNavigationActivity() {

    private var adView: CASNativeView? = null
    private val loadedNativeAds: Queue<NativeAdContent> = LinkedList()

    private val nativeAdCallback = object : NativeAdContentCallback() {
        override fun onNativeAdLoaded(nativeAd: NativeAdContent, ad: AdContentInfo) {
            keepNativeAdInMemory(nativeAd)
            toast("Native Ad loaded: ${loadedNativeAds.size} in total")
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
        title = getString(R.string.adFormats_multipleNativeAd)

        setContent {
            CASAndroidTheme {
                MultipleNativeAdScreen(
                    createAdView = { context -> createNativeAdView(context) },
                    onLoadClick = { loadNativeAd() },
                    onShowClick = { showNextNativeAdContent() }
                )
            }
        }
    }

    override fun onDestroy() {
        for (ad in loadedNativeAds) {
            ad.destroy()
        }
        adView = null
        super.onDestroy()
    }

    private fun loadNativeAd() {
        val adLoader = CASNativeLoader(this, CAS_ID, nativeAdCallback).apply {
            adChoicesPlacement = AdChoicesPlacement.TOP_LEFT
            isStartVideoMuted = true
        }
        val maxNumberOfAdsToLoad = 3
        adLoader.load(maxNumberOfAdsToLoad)
    }

    private fun keepNativeAdInMemory(nativeAd: NativeAdContent) {
        loadedNativeAds.add(nativeAd)
    }

    private fun showNextNativeAdContent() {
        val nativeAd = loadedNativeAds.poll()
        if (nativeAd != null) {
            if (!nativeAd.isExpired) {
                val view = adView
                if (view != null) {
                    view.visibility = View.VISIBLE
                    view.setNativeAd(nativeAd)
                    toast("Native Ad shown: ${loadedNativeAds.size} left")
                } else {
                    toastError("Native view is not ready")
                }
            } else {
                showNextNativeAdContent()
            }
        } else {
            toastError("Native Ad not loaded")
        }
    }

    internal fun createNativeAdView(context: Context): CASNativeView {
        adView?.let { return it }

        val view = CASNativeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setAdTemplateSize(AdSize.MEDIUM_RECTANGLE)
            visibility = View.GONE
        }

        adView = view
        return view
    }
}

@Composable
fun MultipleNativeAdScreen(
    createAdView: (Context) -> CASNativeView,
    onLoadClick: () -> Unit,
    onShowClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 32.dp),
                    factory = { context -> createAdView(context) }
                )

                Button(
                    onClick = onLoadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Text(text = stringResource(id = R.string.load))
                }

                Button(
                    onClick = onShowClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.showNext))
                }
            }
        }
    }
}
