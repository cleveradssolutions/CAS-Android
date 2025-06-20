package com.cleveradssolutions.sampleapp.banner.xml;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.util.Util;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.AdViewListener;
import com.cleversolutions.ads.android.CASBannerView;

public class AdaptiveBannerAdActivity extends BackNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adaptive_ad_activity);
        setTitle(R.string.adFormats_adaptiveBannerAd);

        CASBannerView banner = findViewById(R.id.bannerView);
        banner.setCasId(CAS_ID);
        banner.setAutoloadEnabled(false);

        banner.setAdListener(new AdViewListener() {
            @Override
            public void onAdViewLoaded(@NonNull CASBannerView view) {
                Util.toast(AdaptiveBannerAdActivity.this, "Banner Ad loaded");
            }

            @Override
            public void onAdViewFailed(@NonNull CASBannerView view, @NonNull AdError error) {
                Util.toastError(AdaptiveBannerAdActivity.this, "Banner Ad failed to load: " + error.getMessage());
            }

            @Override
            public void onAdViewPresented(@NonNull CASBannerView view, @NonNull AdStatusHandler info) {
                Util.toast(AdaptiveBannerAdActivity.this, "Banner Ad shown");
            }

            @Override
            public void onAdViewClicked(@NonNull CASBannerView view) {
                Util.toast(AdaptiveBannerAdActivity.this, "Banner Ad clicked");
            }
        });

        banner.setOnImpressionListener(ad ->
                Util.toast(this, "Banner Ad impression: " + ad.getRevenue())
        );

        banner.load();
    }

}