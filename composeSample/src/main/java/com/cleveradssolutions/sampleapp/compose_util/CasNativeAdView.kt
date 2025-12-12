package com.cleveradssolutions.sampleapp.compose_util

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sdk.nativead.*

/**
 * [START cas_native_ad_view]
 * Compose wrapper for `CASNativeView`.
 *
 * This component hosts a fully managed CAS native ad container inside Jetpack Compose.
 * Asset views (headline, icon, media, etc.) are attached inside the `content` lambda.
 *
 * @param nativeAd The loaded `NativeAdContent` instance to display.
 * @param modifier Optional modifier for the ad container.
 * @param content Composable block where asset views are registered to the CASNativeView.
 */
@Composable
fun CasNativeAdView(
    nativeAd: NativeAdContent,
    modifier: Modifier = Modifier,
    content: @Composable (CASNativeView) -> Unit,
) {
    val context = LocalContext.current

    // Stable CASNativeView instance for correct lifecycle and rendering.
    val adView = remember {
        CASNativeView(context).apply {
            id = View.generateViewId()
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    // Register asset views.
    content(adView)

    // Bind native ad to underlying CAS view.
    SideEffect { adView.setNativeAd(nativeAd) }

    // Render CASNativeView within Compose hierarchy.
    AndroidView(
        modifier = modifier,
        factory = { adView }
    )
}
/**
 * [END cas_native_ad_view]
 */


/** Asset: Headline text element. */
@Composable
fun CasNativeHeadlineView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                nativeView.headlineView = this
            }
        }
    )
}

/** Asset: Body text element. */
@Composable
fun CasNativeBodyView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                nativeView.bodyView = this
            }
        }
    )
}

/** Asset: Icon (app icon or advertiser logo). */
@Composable
fun CasNativeIconView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                id = View.generateViewId()
                adjustViewBounds = true
                nativeView.iconView = this
            }
        }
    )
}

/** Asset: Media content (image or video). */
@Composable
fun CasNativeMediaView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASMediaView(context).apply {
                id = View.generateViewId()
                nativeView.mediaView = this
            }
        }
    )
}

/** Asset: Call-to-action button. */
@Composable
fun CasNativeCallToActionView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Button(context).apply {
                id = View.generateViewId()
                nativeView.callToActionView = this
            }
        }
    )
}

/** Asset: Store name label. */
@Composable
fun CasNativeStoreView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                nativeView.storeView = this
            }
        }
    )
}

/** Asset: Price label. */
@Composable
fun CasNativePriceView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                nativeView.priceView = this
            }
        }
    )
}

/** Asset: Star rating element. */
@Composable
fun CasNativeStarRatingView(nativeView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASStarRatingView(context).apply {
                id = View.generateViewId()
                nativeView.starRatingView = this
            }
        }
    )
}
