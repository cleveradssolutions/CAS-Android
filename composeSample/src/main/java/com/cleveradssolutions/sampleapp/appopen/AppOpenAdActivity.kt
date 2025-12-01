package com.cleveradssolutions.sampleapp.appopen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity
import com.cleveradssolutions.sampleapp.support.toast
import com.cleveradssolutions.sampleapp.support.toastError
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.CASAppOpen
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError

class AppOpenAdActivity : BackNavigationActivity() {

    private lateinit var appOpenAd: CASAppOpen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.adFormats_appOpenAd)

        appOpenAd = CASAppOpen(CAS_ID).apply {
            isAutoloadEnabled = false
            isAutoshowEnabled = false

            contentCallback = object : ScreenAdContentCallback() {
                override fun onAdLoaded(ad: AdContentInfo) {
                    toast("App Open Ad loaded")
                }

                override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
                    toastError("App Open Ad failed to load: ${error.message}")
                }

                override fun onAdFailedToShow(format: AdFormat, error: AdError) {
                    toastError("App Open Ad show failed: ${error.message}")
                }

                override fun onAdShowed(ad: AdContentInfo) {
                    toast("App Open Ad shown")
                }

                override fun onAdClicked(ad: AdContentInfo) {
                    toast("App Open Ad clicked")
                }

                override fun onAdDismissed(ad: AdContentInfo) {
                    toast("App Open Ad closed")
                }
            }

            onImpressionListener = OnAdImpressionListener { ad ->
                toast("App Open Ad impression: ${ad.revenue}")
            }
        }

        val activity = this@AppOpenAdActivity

        setContent {
            CASAndroidTheme {
                AppOpenAdScreen(
                    onLoadClick = { appOpenAd.load(activity) },
                    onShowClick = { appOpenAd.show(activity) }
                )
            }
        }
    }
}

@Composable
fun AppOpenAdScreen(
    onLoadClick: () -> Unit,
    onShowClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
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
                Text(text = stringResource(id = R.string.show))
            }
        }
    }
}
