package com.cleveradssolutions.sampleapp;

import android.app.Application;
import android.util.Log;

import com.cleversolutions.ads.Audience;
import com.cleversolutions.ads.ConsentFlow;
import com.cleversolutions.ads.android.CAS;

public class SampleApplication extends Application {

    public static final String TAG = "CAS Sample";
    public static final String CAS_ID = "demo";

    @Override
    public void onCreate() {
        super.onCreate();

        CAS.buildManager()
                .withCasId(CAS_ID)
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
