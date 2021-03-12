package com.cleversolutions.ads.android.sampleapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.cleversolutions.ads.AdCallback;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.CASAppOpen;
import com.cleversolutions.ads.LoadAdCallback;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

class SampleAppOpenAd extends Activity {
    private boolean loadingAppResInProgress = false;
    private boolean appOpenAdVisible = false;

    private TextView appOpenAdStatusView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_app_open_ad);
        appOpenAdStatusView = (TextView) findViewById(R.id.appOpenAdStatusView);

        simulationLongAppResourcesLoading();

        createAppOpenAd();
    }

    @SuppressLint("SetTextI18n")
    private void createAppOpenAd() {
        // Try get last initialized MediationManager
        MediationManager initializedManager = CAS.getManager();

        // Create an Ad
        CASAppOpen appOpenAd;
        if (initializedManager == null)
            // Replace demo to own identifier of MediationManager
            appOpenAd = CASAppOpen.create("demo");
        else
            appOpenAd = CASAppOpen.create(initializedManager);

        // Handle fullscreen callback events
        appOpenAd.setContentCallback(new AdCallback() {
            @Override
            public void onShown(@NotNull AdStatusHandler adStatusHandler) {
                Log.d(SampleActivity.TAG, "App Open Ad shown");
                appOpenAdStatusView.setText("Show AppOpenAd");
            }

            @Override
            public void onShowFailed(@NotNull String message) {
                Log.d(SampleActivity.TAG, "App Open Ad show failed: " + message);
                appOpenAdStatusView.setText("Show AppOpenAd failed: " + message);

                appOpenAdVisible = false;
                startNextActivity();
            }

            @Override
            public void onClosed() {
                Log.d(SampleActivity.TAG, "App Open Ad closed");
                appOpenAdStatusView.setText("AppOpenAd closed");

                appOpenAdVisible = false;
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
        appOpenAdStatusView.setText("Loading AppOpenAd...");
        appOpenAd.loadAd(
                this,
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE,
                new LoadAdCallback() {
                    @Override
                    public void onAdLoaded() {
                        if (!loadingAppResInProgress)
                            return;

                        Log.d(SampleActivity.TAG, "App Open Ad loaded");
                        appOpenAdStatusView.setText("AppOpenAd loaded");

                        appOpenAdVisible = true;
                        appOpenAd.show(SampleAppOpenAd.this);
                    }

                    @Override
                    public void onAdFailedToLoad(@NotNull AdError adError) {
                        if (!loadingAppResInProgress)
                            return;

                        Log.e(SampleActivity.TAG, "App Open Ad failed to load: " + adError.getMessage());
                        appOpenAdStatusView.setText("Load AppOpenAd failed: " + adError.getMessage());

                        startNextActivity();
                    }
                }
        );
    }

    private void startNextActivity() {
        if (loadingAppResInProgress || appOpenAdVisible)
            return;
        Intent intent = new Intent(this, SampleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void simulationLongAppResourcesLoading() {
        // Simulation of long application resources loading for 10 seconds.
        loadingAppResInProgress = true;
        TextView timerText = (TextView) findViewById(R.id.timerView);
        CountDownTimer timer = new CountDownTimer(TimeUnit.SECONDS.toMillis(10), 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + " seconds left");
            }

            @Override
            public void onFinish() {
                loadingAppResInProgress = false;
                startNextActivity();
            }
        };
        timer.start();
    }
}
