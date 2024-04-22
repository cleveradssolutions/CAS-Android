package com.cleveradssolutions.sampleapp;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;
import static com.cleveradssolutions.sampleapp.SampleApplication.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cleversolutions.ads.AdCallback;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.CASAppOpen;
import com.cleversolutions.ads.LoadAdCallback;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SampleAppOpenAdActivity extends Activity {
    private boolean isLoadingAppResources = false;
    private boolean isVisibleAppOpenAd = false;
    private boolean isCompletedSplash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_app_open_ad);

        simulationLongAppResourcesLoading();

        createAppOpenAd();
    }

    @SuppressLint("SetTextI18n")
    private void createAppOpenAd() {
        // Create an Ad
        CASAppOpen appOpenAd = CASAppOpen.create(CAS_ID);

        // Handle fullscreen callback events
        appOpenAd.setContentCallback(new AdCallback() {
            @Override
            public void onShown(@NotNull AdStatusHandler adStatusHandler) {
                Log.d(TAG, "App Open Ad shown");
            }

            @Override
            public void onShowFailed(@NotNull String message) {
                Log.d(TAG, "App Open Ad show failed: " + message);

                isVisibleAppOpenAd = false;
                startNextActivity();
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "App Open Ad closed");

                isVisibleAppOpenAd = false;
                startNextActivity();
            }

            @Override
            public void onClicked() {
                // Not used
            }

            @Override
            public void onComplete() {
                // Not used
            }
        });

        // Load the Ad
        appOpenAd.loadAd(this, new LoadAdCallback() {
                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "App Open Ad loaded");
                        if (isLoadingAppResources) {
                            isVisibleAppOpenAd = true;
                            appOpenAd.show(SampleAppOpenAdActivity.this);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NotNull AdError adError) {
                        Log.e(TAG, "App Open Ad failed to load: " + adError.getMessage());
                        startNextActivity();
                    }
                }
        );
    }

    private void startNextActivity() {
        if (isLoadingAppResources || isVisibleAppOpenAd || isCompletedSplash)
            return;
        isCompletedSplash = true;
        Intent intent = new Intent(this, SampleActivity.class);
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
