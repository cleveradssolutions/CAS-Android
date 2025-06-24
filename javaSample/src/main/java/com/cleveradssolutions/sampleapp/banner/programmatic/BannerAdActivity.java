package com.cleveradssolutions.sampleapp.banner.programmatic;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.support.Util;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdSize;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.AdViewListener;
import com.cleversolutions.ads.android.CASBannerView;

public class BannerAdActivity extends BackNavigationActivity {

    AdViewListener bannerAdListener = new AdViewListener() {
        @Override
        public void onAdViewLoaded(@NonNull CASBannerView view) {
            Util.toast(BannerAdActivity.this, "Banner Ad loaded");
        }

        @Override
        public void onAdViewFailed(@NonNull CASBannerView view, @NonNull AdError error) {
            Util.toastError(BannerAdActivity.this, "Banner Ad failed to load: " + error.getMessage());
        }

        @Override
        public void onAdViewPresented(@NonNull CASBannerView view, @NonNull AdStatusHandler info) {
            Util.toast(BannerAdActivity.this, "Banner Ad shown");
        }

        @Override
        public void onAdViewClicked(@NonNull CASBannerView view) {
            Util.toast(BannerAdActivity.this, "Banner Ad clicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        setTitle(R.string.adFormats_bannerAd);

        FrameLayout container = findViewById(R.id.container);

        CASBannerView banner = new CASBannerView(this, CAS_ID);
        banner.setAdListener(bannerAdListener);
        // banner.setSize(AdSize.BANNER);
        // banner.setSize(AdSize.MEDIUM_RECTANGLE);
        banner.setSize(AdSize.getAdaptiveBannerInScreen(this));
        banner.setAutoloadEnabled(true); // by default

        banner.setOnImpressionListener(ad -> Util.toast(this, "Banner Ad impression: " + ad.getRevenue()));

        container.addView(banner, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        banner.load();
    }

}