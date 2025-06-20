package com.cleveradssolutions.sampleapp.rewarded;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.util.Util;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.AdFormat;
import com.cleveradssolutions.sdk.screen.CASRewarded;
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback;
import com.cleversolutions.ads.AdError;

public class RewardedAdActivity extends BackNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_ad_activity);
        setTitle(R.string.adFormats_rewardedAd);

        CASRewarded rewardedAd = new CASRewarded(CAS_ID);
        rewardedAd.setAutoloadEnabled(false);

        rewardedAd.setContentCallback(new ScreenAdContentCallback() {
            @Override
            public void onAdLoaded(@NonNull AdContentInfo ad) {
                Util.toast(RewardedAdActivity.this, "Rewarded Ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdFormat format, @NonNull AdError error) {
                Util.toastError(RewardedAdActivity.this, "Rewarded Ad failed to load: " + error.getMessage());
            }

            @Override
            public void onAdFailedToShow(@NonNull AdFormat format, @NonNull AdError error) {
                Util.toastError(RewardedAdActivity.this, "Rewarded Ad show failed: " + error.getMessage());
            }

            @Override
            public void onAdShowed(@NonNull AdContentInfo ad) {
                Util.toast(RewardedAdActivity.this, "Rewarded Ad shown");
            }

            @Override
            public void onAdClicked(@NonNull AdContentInfo ad) {
                Util.toast(RewardedAdActivity.this, "Rewarded Ad clicked");
            }

            @Override
            public void onAdDismissed(@NonNull AdContentInfo ad) {
                Util.toast(RewardedAdActivity.this, "Rewarded Ad closed");
            }
        });

        rewardedAd.setOnImpressionListener(ad ->
                Util.toast(this, "Rewarded Ad impression: " + ad.getRevenue())
        );

        findViewById(R.id.load).setOnClickListener(v -> rewardedAd.load(this));
        findViewById(R.id.show).setOnClickListener(v -> rewardedAd.show(this,
                (ad) -> Util.toast(this, "Reward earned"))
        );
    }

}