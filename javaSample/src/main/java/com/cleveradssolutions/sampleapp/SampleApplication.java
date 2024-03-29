package com.cleveradssolutions.sampleapp;

import android.app.Application;
import android.util.Log;

import com.cleversolutions.ads.AdType;
import com.cleversolutions.ads.Audience;
import com.cleversolutions.ads.ConsentFlow;
import com.cleversolutions.ads.MediationManager;
import com.cleversolutions.ads.android.CAS;

public class SampleApplication extends Application {
    public static final String TAG = "CAS Sample";
    public static final String CAS_ID = "demo";
    public static MediationManager adManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // Set Ads Settings
        CAS.settings.setDebugMode(BuildConfig.DEBUG);
        CAS.settings.setTaggedAudience(Audience.NOT_CHILDREN);

        // Set Manual loading mode to disable auto requests
        //CAS.settings.setLoadingMode(LoadingManagerMode.Manual);

        // Initialize SDK
        adManager = CAS.buildManager()
                .withManagerId(CAS_ID)
                .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded)
                .withTestAdMode(BuildConfig.DEBUG)
                .withConsentFlow(
                        new ConsentFlow(true)
                                .withDismissListener(status -> {
                                    Log.d(TAG, "Consent Flow dismissed");
                                })
                )
                .withCompletionListener(config -> {
                    if (config.getError() == null) {
                        Log.d(TAG, "Ad manager initialized");
                    } else {
                        Log.d(TAG, "Ad manager initialization failed: " + config.getError());
                    }
                })
                .build(this);
    }
}
