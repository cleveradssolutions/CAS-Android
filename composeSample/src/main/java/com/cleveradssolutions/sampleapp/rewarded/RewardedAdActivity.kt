package com.cleveradssolutions.sampleapp.rewarded

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
import com.cleveradssolutions.sdk.screen.CASRewarded
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError

class RewardedAdActivity : BackNavigationActivity() {

    private lateinit var rewardedAd: CASRewarded

    private val screenContentCallback = object : ScreenAdContentCallback() {
        override fun onAdLoaded(ad: AdContentInfo) {
            toast("Rewarded Ad loaded")
        }

        override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
            toastError("Rewarded Ad failed to load: ${error.message}")
        }

        override fun onAdFailedToShow(format: AdFormat, error: AdError) {
            toastError("Rewarded Ad show failed: ${error.message}")
        }

        override fun onAdShowed(ad: AdContentInfo) {
            toast("Rewarded Ad shown")
        }

        override fun onAdClicked(ad: AdContentInfo) {
            toast("Rewarded Ad clicked")
        }

        override fun onAdDismissed(ad: AdContentInfo) {
            toast("Rewarded Ad closed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.adFormats_rewardedAd)

        rewardedAd = CASRewarded(CAS_ID).apply {
            contentCallback = screenContentCallback
            isAutoloadEnabled = false

            onImpressionListener = OnAdImpressionListener { ad ->
                toast("Rewarded Ad impression: ${ad.revenue}")
            }
        }

        val activity = this

        setContent {
            CASAndroidTheme {
                RewardedAdScreen(
                    onLoadClick = { rewardedAd.load(activity) },
                    onShowClick = {
                        rewardedAd.show(activity) {
                            toast("Reward earned")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RewardedAdScreen(
    onLoadClick: () -> Unit,
    onShowClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.weight(1f))

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

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
