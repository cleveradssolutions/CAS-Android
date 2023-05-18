package com.cleversolutions.ads.android.sampleapp;

import android.app.Application;
import android.util.Log;

import com.cleversolutions.ads.AdType;
import com.cleversolutions.ads.Audience;
import com.cleversolutions.ads.ConsentFlow;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;

public class SampleApplication extends Application {
    public static final String TAG = "CAS Sample";
    public static MediationManager adManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // Set Ads Settings
        CAS.settings.setDebugMode(true);
        CAS.settings.setTaggedAudience(Audience.NOT_CHILDREN);

        // Set Manual loading mode to disable auto requests
        //CAS.settings.setLoadingMode(LoadingManagerMode.Manual);

        // Initialize SDK
        adManager = CAS.buildManager()
                .withManagerId("demo")
                .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
                .withTestAdMode(true)
                .withConsentFlow(
                        new ConsentFlow(true)
                        //.withPrivacyPolicy("http://")
                )
                .withCompletionListener(config -> {
                    if (config.getError() == null)
                        Log.d(TAG, "Ad manager initialized");
                    else
                        Log.d(TAG, "Ad manager initialization failed: " + config.getError());
                })
                .initialize(this);
    }
}
