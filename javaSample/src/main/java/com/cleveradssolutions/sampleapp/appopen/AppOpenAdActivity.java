package com.cleveradssolutions.sampleapp.appopen;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.support.Util;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.AdFormat;
import com.cleveradssolutions.sdk.screen.CASAppOpen;
import com.cleveradssolutions.sdk.screen.ScreenAdContentCallback;
import com.cleversolutions.ads.AdError;

public class AppOpenAdActivity extends BackNavigationActivity {

    ScreenAdContentCallback screenAdContentCallback = new ScreenAdContentCallback() {
        @Override
        public void onAdLoaded(@NonNull AdContentInfo ad) {
            Util.toast(AppOpenAdActivity.this, "App Open Ad loaded");
        }

        @Override
        public void onAdFailedToLoad(@NonNull AdFormat format, @NonNull AdError error) {
            Util.toastError(AppOpenAdActivity.this, "App Open Ad failed to load: " + error.getMessage());
        }

        @Override
        public void onAdFailedToShow(@NonNull AdFormat format, @NonNull AdError error) {
            Util.toastError(AppOpenAdActivity.this, "App Open Ad show failed: " + error.getMessage());
        }

        @Override
        public void onAdShowed(@NonNull AdContentInfo ad) {
            Util.toast(AppOpenAdActivity.this, "App Open Ad shown");
        }

        @Override
        public void onAdClicked(@NonNull AdContentInfo ad) {
            Util.toast(AppOpenAdActivity.this, "App Open Ad clicked");
        }

        @Override
        public void onAdDismissed(@NonNull AdContentInfo ad) {
            Util.toast(AppOpenAdActivity.this, "App Open Ad closed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_ad_activity);
        setTitle(R.string.adFormats_appOpenAd);

        CASAppOpen appOpenAd = new CASAppOpen(CAS_ID);
        appOpenAd.setContentCallback(screenAdContentCallback);
        appOpenAd.setAutoloadEnabled(false); // by default
        appOpenAd.setAutoshowEnabled(false); // by default

        appOpenAd.setOnImpressionListener(ad ->
                Util.toast(this, "App Open Ad impression: " + ad.getRevenue())
        );

        findViewById(R.id.load).setOnClickListener(v -> appOpenAd.load(this));
        findViewById(R.id.show).setOnClickListener(v -> appOpenAd.show(this));
    }

}
