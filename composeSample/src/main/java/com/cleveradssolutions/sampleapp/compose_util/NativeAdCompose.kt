package com.cleveradssolutions.sampleapp.compose_util

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cleveradssolutions.sdk.nativead.CASMediaView
import com.cleveradssolutions.sdk.nativead.CASNativeView
import com.cleveradssolutions.sdk.nativead.CASStarRatingView
import com.cleveradssolutions.sdk.nativead.NativeAdContent

/**
 * [START cas_native_ad_view]
 * Compose wrapper for `CASNativeView`.
 *
 * This component hosts a fully managed CAS native ad container inside Jetpack Compose.
 * Asset views (headline, icon, media, etc.) are attached inside the `content` lambda.
 *
 * @param adContent The loaded `NativeAdContent` instance to display.
 * @param modifier Optional modifier for the ad container.
 * @param content Composable block where asset views are registered to the CASNativeView.
 */
@Composable
fun NativeAdView(
    adContent: NativeAdContent,
    modifier: Modifier = Modifier,
    content: @Composable (CASNativeView) -> Unit,
) {
    val context = LocalContext.current

    // CASNativeView must be stable across recompositions.
    val adView = remember {
        CASNativeView(context).apply {
            id = View.generateViewId()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            // Ensure Compose content is hosted INSIDE CASNativeView.
            val composeView =
                ComposeView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

            // Replace any previous child (just in case).
            adView.removeAllViews()
            adView.addView(composeView)

            // Compose renders inside CASNativeView.
            composeView.setContent { content(adView) }

            adView
        },
        update = {
            // Re-compose the inner content when outer recomposes.
            val composeView = adView.getChildAt(0) as? ComposeView
            composeView?.setContent { content(adView) }
        }
    )

    // Bind ad AFTER views are registered.
    SideEffect { adView.setNativeAd(adContent) }
}
/**
 * [END cas_native_ad_view]
 */

/** Asset: Headline text element. */
@Composable
fun NativeAdHeadlineView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.headlineView = this
            }
        },
        update = { view -> adView.headlineView = view }
    )
}

/** Asset: Body text element. */
@Composable
fun NativeAdBodyView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.bodyView = this
            }
        },
        update = { view -> adView.bodyView = view }
    )
}

/** Asset: Icon (app icon or advertiser logo). */
@Composable
fun NativeAdIconView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                id = View.generateViewId()
                adjustViewBounds = true
                adView.iconView = this
            }
        },
        update = { view -> adView.iconView = view }
    )
}

/** Asset: Media content (image or video). */
@Composable
fun NativeAdMediaView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASMediaView(context).apply {
                id = View.generateViewId()
                adView.mediaView = this
            }
        },
        update = { view -> adView.mediaView = view }
    )
}

/** Asset: Call-to-action button. */
@Composable
fun NativeAdCallToActionView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Button(context).apply {
                id = View.generateViewId()
                adView.callToActionView = this
            }
        },
        update = { view -> adView.callToActionView = view }
    )
}

/** Asset: Store name label. */
@Composable
fun NativeAdStoreView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.storeView = this
            }
        },
        update = { view -> adView.storeView = view }
    )
}

/** Asset: Price label. */
@Composable
fun NativeAdPriceView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.priceView = this
            }
        },
        update = { view -> adView.priceView = view }
    )
}

/** Asset: Star rating element. */
@Composable
fun NativeAdStarRatingView(adView: CASNativeView, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASStarRatingView(context).apply {
                id = View.generateViewId()
                adView.starRatingView = this
            }
        },
        update = { view -> adView.starRatingView = view }
    )
}
