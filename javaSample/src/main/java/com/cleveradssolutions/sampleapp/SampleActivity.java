package com.cleveradssolutions.sampleapp;

import static com.cleveradssolutions.sampleapp.SampleApplication.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cleversolutions.ads.AdCallback;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdLoadCallback;
import com.cleversolutions.ads.AdPaidCallback;
import com.cleversolutions.ads.AdSize;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.AdType;
import com.cleversolutions.ads.AdViewListener;
import com.cleversolutions.ads.LoadingManagerMode;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;
import com.cleversolutions.ads.android.CASBannerView;
import com.cleveradssolutions.sampleapp.R;

import java.util.Objects;

@SuppressLint("SetTextI18n")
public class SampleActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get current SDK version
        ((TextView) findViewById(R.id.casVersionText)).setText(CAS.getSDKVersion());

        MediationManager adManager = Objects.requireNonNull(SampleApplication.adManager);
        createBanner(adManager);
        createInterstitial(adManager);
        createRewarded(adManager);
    }

    private void createBanner(MediationManager manager) {
        LinearLayout container = findViewById(R.id.container);
        CASBannerView bannerView = new CASBannerView(this, manager);

        // Set required Ad size
        bannerView.setSize(AdSize.getAdaptiveBannerInScreen(this));
        //bannerView.setSize(AdSize.BANNER);
        //bannerView.setSize(AdSize.LEADERBOARD);
        //bannerView.setSize(AdSize.MEDIUM_RECTANGLE);

        TextView label = findViewById(R.id.bannerStatusText);
        // Set Ad content listener
        bannerView.setAdListener(new AdViewListener() {
            @Override
            public void onAdViewPresented(@NonNull CASBannerView casBannerView, @NonNull AdStatusHandler adStatusHandler) {
                label.setText("Presented: " + adStatusHandler.getNetwork());
                Log.d(TAG, "Banner Ad presented from " + adStatusHandler.getNetwork());
            }

            @Override
            public void onAdViewLoaded(@NonNull CASBannerView casBannerView) {
                label.setText("Loaded");
                Log.d(TAG, "Banner Ad loaded and ready to present");
            }

            @Override
            public void onAdViewFailed(@NonNull CASBannerView casBannerView, @NonNull AdError adError) {
                label.setText(adError.getMessage());
                Log.e(TAG, "Banner Ad received error: " + adError.getMessage());
            }

            @Override
            public void onAdViewClicked(@NonNull CASBannerView casBannerView) {
                label.setText("Clicked");
                Log.d(TAG, "Banner Ad received Click action");
            }
        });

        // Add view to container
        container.addView(bannerView);

        // Set controls
        findViewById(R.id.loadBannerBtn).setOnClickListener(v -> {
            label.setText("Loading");
            bannerView.loadNextAd();
        });

        findViewById(R.id.showBannerBtn).setOnClickListener(v -> {
            bannerView.setVisibility(View.VISIBLE);
        });

        findViewById(R.id.hideBannerBtn).setOnClickListener(v -> {
            bannerView.setVisibility(View.GONE);
        });
    }

    private void createInterstitial(MediationManager manager) {
        TextView label = findViewById(R.id.interStatusText);

        // Set Ad load callback
        manager.getOnAdLoadEvent().add(new AdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdType adType) {
                if (adType == AdType.Interstitial) {
                    Log.d(TAG, "Interstitial Ad loaded and ready to show");
                    runOnUiThread(() -> {
                        label.setText("Loaded");
                    });
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdType adType, @Nullable String error) {
                if (adType == AdType.Interstitial) {
                    Log.d(TAG, "Interstitial Ad received error: " + error);
                    runOnUiThread(() -> {
                        label.setText(error);
                    });
                }
            }
        });

        // Create Ad content callback
        AdCallback contentCallback = new AdPaidCallback() {
            @Override
            public void onShown(@NonNull AdStatusHandler adStatusHandler) {
                Log.d(TAG,
                        "Interstitial Ad shown from " + adStatusHandler.getNetwork());
            }

            @Override
            public void onAdRevenuePaid(@NonNull AdStatusHandler adStatusHandler) {
                Log.d(TAG,
                        "Rewarded Ad revenue paid from " + adStatusHandler.getNetwork());
            }

            @Override
            public void onShowFailed(@NonNull String s) {
                Log.e(TAG, "Interstitial Ad show failed: " + s);
                label.setText(s);
            }

            @Override
            public void onClicked() {
                Log.d(TAG, "Interstitial Ad received Click");
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "Interstitial Ad received Close");
                label.setText("Closed");
            }
        };

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
        if (CAS.settings.getLoadingMode() == LoadingManagerMode.Manual) {
            findViewById(R.id.loadInterBtn).setOnClickListener(v -> {
                label.setText("Loading");
                manager.loadInterstitial();
            });
        } else {
            if (manager.isInterstitialReady())
                label.setText("Loaded");
            else
                label.setText("Loading");
            findViewById(R.id.loadInterBtn).setVisibility(View.GONE);
        }

        findViewById(R.id.showInterBtn).setOnClickListener(v -> {
            if (manager.isInterstitialReady())
                manager.showInterstitial(this, contentCallback);
            else
                Log.e(TAG, "Interstitial Ad not ready to show");
        });
    }

    private void createRewarded(MediationManager manager) {
        TextView label = findViewById(R.id.rewardedStatusText);

        // Set Ad load callback
        manager.getOnAdLoadEvent().add(new AdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdType adType) {
                if (adType == AdType.Rewarded) {
                    Log.d(TAG, "Interstitial Ad loaded and ready to show");
                    runOnUiThread(() -> {
                        label.setText("Loaded");
                    });
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdType adType, @Nullable String error) {
                if (adType == AdType.Rewarded) {
                    Log.d(TAG, "Interstitial Ad received error: " + error);
                    runOnUiThread(() -> {
                        label.setText(error);
                    });
                }
            }
        });

        // Create Ad content callback
        AdCallback contentCallback = new AdPaidCallback() {
            @Override
            public void onShown(@NonNull AdStatusHandler adStatusHandler) {
                Log.d(TAG,
                        "Rewarded Ad shown from " + adStatusHandler.getNetwork());
            }

            @Override
            public void onAdRevenuePaid(@NonNull AdStatusHandler adStatusHandler) {
                Log.d(TAG,
                        "Rewarded Ad revenue paid from " + adStatusHandler.getNetwork());
            }

            @Override
            public void onShowFailed(@NonNull String s) {
                Log.e(TAG, "Rewarded Ad show failed: " + s);
                label.setText(s);
            }

            @Override
            public void onClicked() {
                Log.d(TAG, "Rewarded Ad received Click");
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onClosed() {
                Log.d(TAG, "Rewarded Ad received Close");
                label.setText("Closed");
            }
        };

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
        if (CAS.settings.getLoadingMode() == LoadingManagerMode.Manual) {
            findViewById(R.id.loadRewardedBtn).setOnClickListener(v -> {
                label.setText("Loading");
                manager.loadRewardedAd();
            });
        } else {
            if (manager.isRewardedAdReady())
                label.setText("Loaded");
            else
                label.setText("Loading");
            findViewById(R.id.loadRewardedBtn).setVisibility(View.GONE);
        }

        findViewById(R.id.showRewardedBtn).setOnClickListener(v -> {
            if (manager.isRewardedAdReady())
                manager.showRewardedAd(this, contentCallback);
            else
                Log.e(TAG, "Rewarded Ad not ready to show");
        });
    }

}
