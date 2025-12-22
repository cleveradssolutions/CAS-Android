package com.cleveradssolutions.sampleapp.formats

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CASBannerView
import kotlin.coroutines.resume
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun LazyBannerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isPreviewMode = LocalInspectionMode.current
    val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp
    val adsToLoad = 5

    // Indicate whether the banner ads are currently being loaded.
    var isLoadingAds by remember { mutableStateOf(true) }
    var loadedAds by remember { mutableStateOf<List<CASBannerView>>(emptyList()) }
    val fillerText = loadFillerText(context)

    // Load ads when on launch of the composition.
    LaunchedEffect(Unit) {
        if (!isPreviewMode) {
            loadedAds = loadBannerAds(context, adsToLoad, deviceCurrentWidth)
        }
        isLoadingAds = false
    }

    // Display a loading indicator if ads are still being fetched.
    if (isLoadingAds) {
        CircularProgressIndicator(
            modifier = modifier
                .width(100.dp)
                .height(100.dp)
        )
    } else {
        // Display a lazy list with loaded ads and filler content.
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(loadedAds) { bannerView ->
                Column {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { bannerView }
                    )
                    // Display the filler content.
                    fillerText.forEach { content ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = content,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }

    // Clean up the banner views after use.
    DisposableEffect(Unit) {
        onDispose { loadedAds.forEach { adView -> adView.destroy() } }
    }
}

@Preview
@Composable
private fun LazyBannerScreenPreview() {
    CASAndroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) { LazyBannerScreen() }
    }
}

private suspend fun loadBannerAd(
    context: Context,
    widthDp: Int
): Result<CASBannerView> {
    return suspendCancellableCoroutine { continuation ->
        val banner = CASBannerView(context, SampleApplication.CAS_ID)
        // Disable autoload before set size
        banner.isAutoloadEnabled = false

        // Inline adaptive banner: width = widthDp, height up to 250dp.
        banner.size = AdSize.getInlineBanner(widthDp, 250)

        banner.adListener =
            object : AdViewListener {
                override fun onAdViewLoaded(view: CASBannerView) {
                    Log.d(TAG, "Lazy CAS banner ad was loaded.")
                    continuation.resume(Result.success(view))
                }

                override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                    Log.e(TAG, "Lazy CAS banner ad failed to load: ${error.message}")
                    view.destroy()
                    continuation.resume(Result.failure(Error(error.message)))
                }

                override fun onAdViewClicked(view: CASBannerView) {
                    Log.d(TAG, "Lazy CAS banner ad was clicked.")
                }
            }

        continuation.invokeOnCancellation {
            // When the coroutine is cancelled, clean up resources.
            banner.destroy()
        }

        banner.load()
    }
}

private suspend fun loadBannerAds(
    context: Context,
    count: Int,
    widthDp: Int
): List<CASBannerView> =
    supervisorScope {
        List(count) {
            async {
                loadBannerAd(context, widthDp).getOrNull()
            }
        }
            .awaitAll()
            .filterNotNull()
    }

private fun loadFillerText(context: Context): List<String> {
    val fillerContent = context.resources.getStringArray(R.array.lazy_banner_filler_content)
    return fillerContent.toList()
}

