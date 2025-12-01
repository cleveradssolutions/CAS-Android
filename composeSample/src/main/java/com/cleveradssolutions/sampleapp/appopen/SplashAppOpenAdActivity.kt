package com.cleveradssolutions.sampleapp.appopen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cleveradssolutions.sampleapp.R
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.CAS_ID
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG
import com.cleveradssolutions.sampleapp.support.SelectionActivity
import com.cleveradssolutions.sampleapp.ui.theme.CASAndroidTheme
import com.cleveradssolutions.sdk.AdContentInfo
import com.cleveradssolutions.sdk.AdFormat
import com.cleveradssolutions.sdk.OnAdImpressionListener
import com.cleveradssolutions.sdk.screen.CASAppOpen
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback
import com.cleversolutions.ads.AdError
import java.util.concurrent.TimeUnit

class SplashAppOpenAdActivity : ComponentActivity() {

    private var isLoadingAppResources = false
    private var isVisibleAppOpenAd = false
    private var isCompletedSplash = false

    private lateinit var appOpenAd: CASAppOpen
    private var timer: CountDownTimer? = null

    private val secondsLeftState = mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CASAndroidTheme {
                SplashAppOpenAdScreen(
                    secondsLeft = secondsLeftState.longValue,
                    onSkipClick = { onSkipClicked() }
                )
            }
        }

        simulationLongAppResourcesLoading()
        createAppOpenAd()
    }

    private fun onSkipClicked() {
        isLoadingAppResources = false
        startNextActivity()
    }

    @SuppressLint("SetTextI18n")
    private fun createAppOpenAd() {
        appOpenAd = CASAppOpen(CAS_ID)

        appOpenAd.contentCallback = object : ScreenAdContentCallback() {
            override fun onAdLoaded(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad loaded")
                if (isLoadingAppResources) {
                    isVisibleAppOpenAd = true
                    appOpenAd.show(this@SplashAppOpenAdActivity)
                }
            }

            override fun onAdFailedToLoad(format: AdFormat, error: AdError) {
                Log.e(TAG, "App Open Ad failed to load: ${error.message}")
                startNextActivity()
            }

            override fun onAdFailedToShow(format: AdFormat, error: AdError) {
                Log.e(TAG, "App Open Ad show failed: ${error.message}")
                isVisibleAppOpenAd = false
                startNextActivity()
            }

            override fun onAdShowed(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad shown")
            }

            override fun onAdClicked(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad clicked")
            }

            override fun onAdDismissed(ad: AdContentInfo) {
                Log.d(TAG, "App Open Ad closed")
                isVisibleAppOpenAd = false
                startNextActivity()
            }
        }

        appOpenAd.onImpressionListener = OnAdImpressionListener { ad ->
            Log.d(TAG, "App Open Ad impression: ${ad.revenue}")
        }

        appOpenAd.load(this)
    }

    private fun startNextActivity() {
        if (isLoadingAppResources || isVisibleAppOpenAd || isCompletedSplash)
            return

        isCompletedSplash = true
        val intent = Intent(this, SelectionActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        timer?.cancel()
    }

    private fun simulationLongAppResourcesLoading() {
        isLoadingAppResources = true
        timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(7), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeftState.longValue =
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
            }

            override fun onFinish() {
                isLoadingAppResources = false
                startNextActivity()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}

@Composable
fun SplashAppOpenAdScreen(
    secondsLeft: Long,
    onSkipClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AppOpenAd loading is carried out before the application resources are ready.",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (secondsLeft > 0)
                    "$secondsLeft seconds left"
                else
                    "",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onSkipClick) {
                Text(text = "Skip AppOpenAd")
            }
        }
    }
}
