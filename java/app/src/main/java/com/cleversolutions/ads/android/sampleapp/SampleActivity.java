package com.cleversolutions.ads.android.sampleapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.AnyThread;
import androidx.annotation.Nullable;

import com.cleversolutions.ads.AdLoadCallback;
import com.cleversolutions.ads.AdSize;
import com.cleversolutions.ads.AdType;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;
import com.cleversolutions.ads.android.CASBannerView;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SampleActivity extends Activity implements AdLoadCallback {
    public static final String TAG = "CAS Sample";

    private HashMap<AdType, TextView> statusAdViews;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusAdViews = new HashMap<>();
        statusAdViews.put(AdType.Banner, (TextView) findViewById(R.id.bannerStatusText));
        statusAdViews.put(AdType.Interstitial, (TextView) findViewById(R.id.interStatusText));
        statusAdViews.put(AdType.Rewarded, (TextView) findViewById(R.id.rewardedStatusText));

        // Try get last initialized MediationManager
        MediationManager manager = Objects.requireNonNull(CAS.getManager());

        // Check ad available
        for (Map.Entry<AdType, TextView> entry : statusAdViews.entrySet()) {
            entry.getValue().setText(manager.isAdReady(entry.getKey()) ? "Ready" : "Loading");
        }

        // Validate Integration
        CAS.validateIntegration(this);

        // Get current SDK version
        ((TextView) findViewById(R.id.casVersionText)).setText("version: " + CAS.getSDKVersion());

        // Subscribe loading ad event
        manager.getOnAdLoadEvent().add(this);

        // Create Banner View
        CASBannerView bannerView = new CASBannerView(this);
        bannerView.setListener(new AdContentListener(this, AdType.Banner));
        bannerView.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        // Attach banner View
        addContentView(
                bannerView,
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        ((Button) findViewById(R.id.setDefaultSizeBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setSize(AdSize.BANNER));

        ((Button) findViewById(R.id.setAdaptiveSizeBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setSize(AdSize.getAdaptiveBannerInScreen(SampleActivity.this)));

        ((Button) findViewById(R.id.setLedearboardSizeBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setSize(AdSize.LEADERBOARD));

        ((Button) findViewById(R.id.setMrecSizeBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setSize(AdSize.MEDIUM_RECTANGLE));

        ((Button) findViewById(R.id.showBannerBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setVisibility(View.VISIBLE));

        ((Button) findViewById(R.id.hideBannerBtn)).setOnClickListener((View.OnClickListener) v ->
                bannerView.setVisibility(View.GONE));

        ((Button) findViewById(R.id.showInterBtn)).setOnClickListener((View.OnClickListener) v ->
                CAS.getManager().showInterstitial(
                        SampleActivity.this,
                        new AdContentListener(this, AdType.Interstitial)
                ));

        ((Button) findViewById(R.id.showRewardedBtn)).setOnClickListener((View.OnClickListener) v ->
                CAS.getManager().showRewardedAd(
                        SampleActivity.this,
                        new AdContentListener(this, AdType.Rewarded)
                ));
    }

    @SuppressLint("SetTextI18n")
    @AnyThread
    @Override
    public void onAdLoaded(@NotNull AdType adType) {
        runOnUiThread(() -> {
            Log.d(TAG, "$type Ad Loaded");
            statusAdViews.get(adType).setText("Ready");
        });
    }

    @AnyThread
    @Override
    public void onAdFailedToLoad(@NotNull AdType adType, @Nullable String error) {
        runOnUiThread(() -> {
            Log.d(TAG, adType.name() + " Ad Failed to Load: " + error);
            statusAdViews.get(adType).setText(error);
        });
    }
}
