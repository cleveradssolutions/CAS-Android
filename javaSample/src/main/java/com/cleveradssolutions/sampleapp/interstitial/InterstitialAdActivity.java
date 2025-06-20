package com.cleveradssolutions.sampleapp.interstitial;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.util.Util;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.AdFormat;
import com.cleveradssolutions.sdk.screen.CASInterstitial;
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback;
import com.cleversolutions.ads.AdError;

public class InterstitialAdActivity extends BackNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_ad_activity);
        setTitle(R.string.adFormats_interstitialAd);

        CASInterstitial interstitialAd = new CASInterstitial(CAS_ID);
        interstitialAd.setAutoloadEnabled(false);
        interstitialAd.setAutoshowEnabled(false);

        interstitialAd.setContentCallback(new ScreenAdContentCallback() {
            @Override
            public void onAdLoaded(@NonNull AdContentInfo ad) {
                Util.toast(InterstitialAdActivity.this, "Interstitial Ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull AdFormat format, @NonNull AdError error) {
                Util.toastError(InterstitialAdActivity.this, "Interstitial Ad failed to load: " + error.getMessage());
            }

            @Override
            public void onAdFailedToShow(@NonNull AdFormat format, @NonNull AdError error) {
                Util.toastError(InterstitialAdActivity.this, "Interstitial Ad show failed: " + error.getMessage());
            }

            @Override
            public void onAdShowed(@NonNull AdContentInfo ad) {
                Util.toast(InterstitialAdActivity.this, "Interstitial Ad shown");
            }

            @Override
            public void onAdClicked(@NonNull AdContentInfo ad) {
                Util.toast(InterstitialAdActivity.this, "Interstitial Ad clicked");
            }

            @Override
            public void onAdDismissed(@NonNull AdContentInfo ad) {
                Util.toast(InterstitialAdActivity.this, "Interstitial Ad closed");
            }
        });

        interstitialAd.setOnImpressionListener(ad ->
                Util.toast(this, "Interstitial Ad impression: " + ad.getRevenue())
        );

        findViewById(R.id.load).setOnClickListener(v -> interstitialAd.load(this));
        findViewById(R.id.show).setOnClickListener(v -> interstitialAd.show(this));
    }

}