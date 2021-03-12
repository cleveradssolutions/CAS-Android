package com.cleversolutions.ads.android.sampleapp;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cleversolutions.ads.AdSize;
import com.cleversolutions.ads.AdTypeFlags;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.OnInitializationListener;
import com.cleversolutions.ads.android.CAS;
import com.cleversolutions.basement.CASAnalytics;

import org.jetbrains.annotations.NotNull;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // (Optional) Apply Analytics receiver
        CASAnalytics.INSTANCE.setHandler((eventName, content) -> {
            Log.i(SampleActivity.TAG, "Analytics log event " + eventName);
            for (String key : content.keySet()) {
                Log.i(SampleActivity.TAG, "\t $key:" + content.get(key));
            }
        });

        // Set Ads Settings
        CAS.getSettings().setDebugMode(true);
        CAS.getSettings().setAnalyticsCollectionEnabled(true);
        CAS.getSettings().setAllowInterstitialAdsWhenVideoCostAreLower(true);

        // Initialize SDK
        MediationManager manager = CAS.buildManager()
                .withManagerId("demo")
                .withEnabledAdTypes(AdTypeFlags.Banner | AdTypeFlags.Interstitial | AdTypeFlags.Rewarded)
                .withTestAdMode(true)
                .withInitListener((success, error) -> {
                    Log.i(SampleActivity.TAG, "CAS initialize success: " + success + " with error: " + error);
                })
                .initialize(this);

        // Set banner size
        manager.setBannerSize(AdSize.BANNER);
    }
}
