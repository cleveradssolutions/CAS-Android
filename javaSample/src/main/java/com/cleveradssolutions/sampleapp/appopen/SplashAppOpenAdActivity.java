package com.cleveradssolutions.sampleapp.appopen;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;
import static com.cleveradssolutions.sampleapp.SampleApplication.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.selection.SelectionActivity;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.AdFormat;
import com.cleveradssolutions.sdk.screen.CASAppOpen;
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback;
import com.cleversolutions.ads.AdError;

import java.util.concurrent.TimeUnit;

public class SplashAppOpenAdActivity extends Activity {

    private boolean isLoadingAppResources = false;
    private boolean isVisibleAppOpenAd = false;
    private boolean isCompletedSplash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_app_open_ad_activity);

        simulationLongAppResourcesLoading();

        createAppOpenAd();
    }

    @SuppressLint("SetTextI18n")
    private void createAppOpenAd() {
        // Create an Ad
        CASAppOpen appOpenAd = new CASAppOpen(this, CAS_ID);
        appOpenAd.setAutoloadEnabled(false);
        appOpenAd.setAutoshowEnabled(false);

        // Handle fullscreen callback events
        appOpenAd.setContentCallback(new ScreenAdContentCallback() {
            @Override
            public void onAdLoaded(@NonNull AdContentInfo adContentInfo) {
                Log.d(TAG, "App Open Ad loaded");
                if (isLoadingAppResources) {
                    isVisibleAppOpenAd = true;
                    appOpenAd.show(SplashAppOpenAdActivity.this);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdFormat adFormat, @NonNull AdError adError) {
                Log.e(TAG, "App Open Ad failed to load: " + adError.getMessage());
                startNextActivity();
            }

            @Override
            public void onAdFailedToShow(@NonNull AdFormat adFormat, @NonNull AdError adError) {
                Log.d(TAG, "App Open Ad show failed: " + adError.getMessage());

                isVisibleAppOpenAd = false;
                startNextActivity();
            }

            @Override
            public void onAdShowed(@NonNull AdContentInfo adContentInfo) {
                Log.d(TAG, "App Open Ad shown");
            }

            @Override
            public void onAdClicked(@NonNull AdContentInfo adContentInfo) {
                Log.d(TAG, "App Open Ad clicked");
            }

            @Override
            public void onAdDismissed(@NonNull AdContentInfo adContentInfo) {
                Log.d(TAG, "App Open Ad closed");

                isVisibleAppOpenAd = false;
                startNextActivity();
            }
        });

        appOpenAd.setOnImpressionListener(adContentInfo ->
                Log.d(TAG, "App Open Ad impression: ${ad.revenue}"));

        // Load the Ad
        appOpenAd.load(this);
    }

    private void startNextActivity() {
        if (isLoadingAppResources || isVisibleAppOpenAd || isCompletedSplash)
            return;
        isCompletedSplash = true;
        Intent intent = new Intent(this, SelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void simulationLongAppResourcesLoading() {
        // Simulation of long application resources loading for 5 seconds.
        isLoadingAppResources = true;
        TextView timerText = findViewById(R.id.timerView);
        CountDownTimer timer = new CountDownTimer(TimeUnit.SECONDS.toMillis(5), 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + " seconds left");
            }

            @Override
            public void onFinish() {
                isLoadingAppResources = false;
                startNextActivity();
            }
        };
        timer.start();

        findViewById(R.id.skipAppOpenAd).setOnClickListener(v -> {
            isLoadingAppResources = false;
            startNextActivity();
        });
    }

}
