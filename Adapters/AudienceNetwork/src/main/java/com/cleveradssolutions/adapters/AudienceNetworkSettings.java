package com.cleveradssolutions.adapters;

import android.util.Log;

import androidx.annotation.Keep;

import com.facebook.ads.internal.settings.AdInternalSettings;

@Keep
public class AudienceNetworkSettings {
    public static void setDataProcessingOptions(String[] options) {
        AdInternalSettings.setDataProcessingOptions(options, (Integer)null, (Integer)null);
    }

    public static void setDataProcessingOptions(String[] options, int country, int state) {
        AdInternalSettings.setDataProcessingOptions(options, country, state);
    }
}
