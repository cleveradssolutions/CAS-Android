package com.cleveradssolutions.compose

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
 * This is the Compose wrapper for a CASNativeView.
 *
 * @param adContent The NativeAdContent object containing the ad assets to be displayed in this view.
 * @param modifier The modifier to apply to the native ad.
 * @param content A composable function that defines the rest of the native ad view's elements.
 */
@Composable
fun NativeAdView(
    adContent: NativeAdContent,
    modifier: Modifier = Modifier,
    content: @Composable (adView: CASNativeView) -> Unit,
) {
    val context = LocalContext.current
    val latestContent by rememberUpdatedState(content)

    val adView = remember { CASNativeView(context).apply { id = View.generateViewId() } }

    val composeView = remember {
        ComposeView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            if (composeView.parent == null) {
                adView.addView(composeView)
            }
            composeView.setContent { latestContent(adView) }
            adView
        },
    )

    SideEffect { adView.setNativeAd(adContent) }
}
/**
 * [END cas_native_ad_view]
 */

/**
 * The ComposeWrapper container for a bodyView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdBodyView(
    adView: CASNativeView,
    modifier: Modifier = Modifier,
    update: (TextView) -> Unit = {}
    ) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.bodyView = this
            }
        },
        update = update,
    )
}

/**
 * The ComposeWrapper container for a callToActionView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdCallToActionView(
    adView: CASNativeView,
    modifier: Modifier = Modifier,
    update: (Button) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Button(context).apply {
                id = View.generateViewId()
                adView.callToActionView = this
            }
        },
        update = update,
    )
}


/**
 * The ComposeWrapper container for a headlineView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdHeadlineView(
    adView: CASNativeView,
    modifier: Modifier = Modifier,
    update: (TextView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.headlineView = this
            }
        },
        update = update,
    )
}

/**
 * The ComposeWrapper container for an iconView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdIconView(
    adView: CASNativeView,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                id = View.generateViewId()
                adjustViewBounds = true
                adView.iconView = this
            }
        },
    )
}

/**
 * The ComposeWrapper for a mediaView inside a CASNativeView. This composable must be invoked from
 * within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdMediaView(
    adView: CASNativeView,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASMediaView(context).apply {
                id = View.generateViewId()
                adView.mediaView = this
            }
        },
    )
}

/**
 * The ComposeWrapper container for a priceView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdPriceView(
    adView: CASNativeView,
    modifier: Modifier = Modifier,
    update: (TextView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.priceView = this
            }
        },
        update = update,
    )
}

/**
 * The ComposeWrapper container for a starRatingView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdStarRatingView(
    adView: CASNativeView,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CASStarRatingView(context).apply {
                id = View.generateViewId()
                adView.starRatingView = this
            }
        },
    )
}

/**
 * The ComposeWrapper container for a storeView inside a CASNativeView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdStoreView(
    adView: CASNativeView,
    modifier: Modifier = Modifier,
    update: (TextView) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                id = View.generateViewId()
                adView.storeView = this
            }
        },
        update = update,
    )
}
