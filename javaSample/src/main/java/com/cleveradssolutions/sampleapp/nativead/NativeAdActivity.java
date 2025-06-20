package com.cleveradssolutions.sampleapp.nativead;

import static com.cleveradssolutions.sampleapp.SampleApplication.CAS_ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.cleveradssolutions.sampleapp.R;
import com.cleveradssolutions.sampleapp.util.BackNavigationActivity;
import com.cleveradssolutions.sampleapp.util.Util;
import com.cleveradssolutions.sdk.AdContentInfo;
import com.cleveradssolutions.sdk.nativead.AdChoicesPlacement;
import com.cleveradssolutions.sdk.nativead.CASNativeLoader;
import com.cleveradssolutions.sdk.nativead.CASNativeView;
import com.cleveradssolutions.sdk.nativead.NativeAdContent;
import com.cleveradssolutions.sdk.nativead.NativeAdContentCallback;
import com.cleversolutions.ads.AdError;

public class NativeAdActivity extends BackNavigationActivity {

    private CASNativeView adView;

    private final NativeAdContentCallback nativeAdCallback = new NativeAdContentCallback() {

        @Override
        public void onNativeAdLoaded(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            Util.toast(NativeAdActivity.this, "Native Ad loaded");
            FrameLayout container = findViewById(R.id.container);
            inflateNativeAdView(container);
            registerNativeAdContent(nativeAd);
        }

        @Override
        public void onNativeAdFailedToLoad(@NonNull AdError error) {
            Util.toastError(NativeAdActivity.this, "Native Ad failed to load: " + error.getMessage());
        }

        @Override
        public void onNativeAdFailedToShow(@NonNull NativeAdContent nativeAd, @NonNull AdError error) {
            Util.toastError(NativeAdActivity.this, "Native Ad show failed: " + error.getMessage());
        }

        @Override
        public void onNativeAdClicked(@NonNull NativeAdContent nativeAd, @NonNull AdContentInfo ad) {
            Util.toast(NativeAdActivity.this, "Native Ad clicked");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_activity);
        setTitle(R.string.adFormats_nativeAd);

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
            adView.setNativeAd(nativeAd);
        }
    }

    private void inflateNativeAdView(FrameLayout container) {
        CASNativeView adView = (CASNativeView) LayoutInflater.from(container.getContext())
                .inflate(R.layout.native_ad_layout, container, false);
        container.addView(adView);
        registerAdAssetViews(adView);
        this.adView = adView;
    }

    private void registerAdAssetViews(CASNativeView adView) {
        // You can also omit adChoicesView and it will be created automatically.
        adView.setAdChoicesView(adView.findViewById(R.id.ad_choices_view));

        adView.setMediaView(adView.findViewById(R.id.ad_media_view));
//        adView.setAdLabelView(adView.findViewById(R.id.ad_label)); // Optional
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setIconView(adView.findViewById(R.id.ad_icon)); // Optional
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action)); // Optional
//        adView.setBodyView(adView.findViewById(R.id.ad_body)); // Optional
//        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser)); // Optional
//        adView.setStoreView(adView.findViewById(R.id.ad_store)); // Optional
//        adView.setPriceView(adView.findViewById(R.id.ad_price)); // Optional
//        adView.setReviewCountView(adView.findViewById(R.id.ad_review_count)); // Optional
        adView.setStarRatingView(adView.findViewById(R.id.ad_star_rating));
    }

}
