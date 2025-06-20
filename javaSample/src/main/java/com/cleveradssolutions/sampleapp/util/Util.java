package com.cleveradssolutions.sampleapp.util;

import static com.cleveradssolutions.sampleapp.SampleApplication.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Util {

    public static void toast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }

    public static void toastError(final Context context, final String error) {
        final String message = "Error: " + error;
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
    }

}
