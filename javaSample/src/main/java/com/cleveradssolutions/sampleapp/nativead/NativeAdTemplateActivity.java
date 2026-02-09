package com.cleveradssolutions.sampleapp.nativead;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.support.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.support.Util;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement;
import com.cleveradssolutions.sdk.nativead.CASNativeLoader;
import com.cleveradssolutions.sdk.nativead.CASNativeView;
import com.cleveradssolutions.sdk.nativead.NativeAdContent;
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback;
import com.cleversolutions.ads.AdError;
import com.cleversolutions.ads.AdSize;

public class NativeAdTemplateActivity extends BackNavigationActivity {

    private CASNativeView adView;

    private final NativeAdContentCallback nativeAdCallback = new NativeAdContentCallback() {

        @Override
        public void onNativeAdLoaded(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            Util.toast(NativeAdTemplateActivity.this, "Native Ad loaded");
            FrameLayout container = findViewById(R.id.container);
            inflateNativeAdView(container);
            registerNativeAdContent(nativeAd);
        }

        @Override
        public void onNativeAdFailedToLoad(@NonNull AdError error) {
            Util.toastError(NativeAdTemplateActivity.this, "Native Ad failed to load: " + error.getMessage());
        }

        @Override
        public void onNativeAdFailedToShow(@NonNull NativeAdContent nativeAd, @NonNull AdError error) {
            Util.toastError(NativeAdTemplateActivity.this, "Native Ad show failed: " + error.getMessage());
        }

        @Override
        public void onNativeAdClicked(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            Util.toast(NativeAdTemplateActivity.this, "Native Ad clicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        setTitle(R.string.adFormats_nativeAdTemplate);

        loadNativeAd();
    }

    private void loadNativeAd() {
        CASNativeLoader adLoader = new CASNativeLoader(this, CAS_ID, nativeAdCallback);
        adLoader.setAdChoicesPlacement(AdChoicesPlacement.TOP_LEFT);
        adLoader.setStartVideoMuted(true);

        adLoader.load();
    }

    private void registerNativeAdContent(NativeAdContent nativeAd) {
        if (adView != null) {
            adView.bindAdContent(nativeAd);
        }
    }

    private void inflateNativeAdView(ViewGroup container) {
        CASNativeView adView = new CASNativeView(this);
        this.adView = adView;
        AdSize size = AdSize.MEDIUM_RECTANGLE;
        adView.setAdTemplateSize(size);

        customizeAdViewAppearance(adView);

        container.addView(adView);
    }

    private void customizeAdViewAppearance(CASNativeView adView) {
        // Default values are shown below:
//        adView.setBackgroundColor(Color.WHITE);

//        if (adView.getCallToActionView() != null) {
//            adView.getCallToActionView().setBackgroundColor(Color.BLUE);
//        }

//        if (adView.getHeadlineView() != null) {
//            adView.getHeadlineView().setTextColor(Color.parseColor("#80000000"));
//        }
    }

}
