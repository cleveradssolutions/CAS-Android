package com.cleveradssolutions.sampleapp.nativead;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;
import android.view.ViewGroup;

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

import java.util.LinkedList;
import java.util.Queue;

public class MultipleNativeAdActivity extends BackNavigationActivity {

    private CASNativeView adView;
    private final Queue<NativeAdContent> loadedNativeAds = new LinkedList<>();

    private final NativeAdContentCallback nativeAdCallback = new NativeAdContentCallback() {

        @Override
        public void onNativeAdLoaded(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            keepNativeAdInMemory(nativeAd);
            Util.toast(MultipleNativeAdActivity.this, "Native Ad loaded: " + loadedNativeAds.size() + " in total");
        }

        @Override
        public void onNativeAdFailedToLoad(@NonNull AdError error) {
            Util.toastError(MultipleNativeAdActivity.this, "Native Ad failed to load: " + error.getMessage());
        }

        @Override
        public void onNativeAdFailedToShow(@NonNull NativeAdContent nativeAd, @NonNull AdError error) {
            Util.toastError(MultipleNativeAdActivity.this, "Native Ad show failed: " + error.getMessage());
        }

        @Override
        public void onNativeAdClicked(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            Util.toast(MultipleNativeAdActivity.this, "Native Ad clicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multiple_native_ad_activity);
        setTitle(R.string.adFormats_multipleNativeAd);

        findViewById(R.id.load).setOnClickListener(v -> loadNativeAd());
        findViewById(R.id.show).setOnClickListener(v -> showNextNativeAdContent());
    }

    @Override
    protected void onDestroy() {
        for (NativeAdContent ad : loadedNativeAds) {
            ad.destroy();
        }
        super.onDestroy();
    }

    private void loadNativeAd() {
        CASNativeLoader adLoader = new CASNativeLoader(this, CAS_ID, nativeAdCallback);
        adLoader.setAdChoicesPlacement(AdChoicesPlacement.TOP_LEFT);
        adLoader.setStartVideoMuted(true);

        int maxNumberOfAdsToLoad = 3;
        adLoader.load(maxNumberOfAdsToLoad);
    }

    private void keepNativeAdInMemory(NativeAdContent nativeAd) {
        loadedNativeAds.add(nativeAd);
    }

    private void showNextNativeAdContent() {
        NativeAdContent nativeAd = loadedNativeAds.poll();
        if (nativeAd != null) {
            if (!nativeAd.isExpired()) {
                getAdView().bindAdContent(nativeAd);
                Util.toast(MultipleNativeAdActivity.this, "Native Ad shown: " + loadedNativeAds.size() + " left");
            } else {
                showNextNativeAdContent();
            }
        } else {
            Util.toastError(MultipleNativeAdActivity.this, "Native Ad not loaded");
        }
    }

    private CASNativeView getAdView() {
        if (adView == null) {
            return inflateNativeAdView(findViewById(R.id.container));
        }
        return adView;
    }

    private CASNativeView inflateNativeAdView(ViewGroup container) {
        CASNativeView adView = new CASNativeView(this);
        this.adView = adView;
        AdSize size = AdSize.MEDIUM_RECTANGLE;
        adView.setAdTemplateSize(size);

        container.addView(adView);

        return adView;
    }

}
