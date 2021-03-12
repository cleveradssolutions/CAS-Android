package com.cleversolutions.ads.android.sampleapp;


import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.cleversolutions.ads.AdCallback;
import com.cleversolutions.ads.AdStatusHandler;
import com.cleversolutions.ads.AdType;

import org.jetbrains.annotations.NotNull;

public class AdContentListener implements AdCallback {

    private final Context context;
    private final AdType type;

    public AdContentListener(Context context, AdType type) {
        this.context = context;
        this.type = type;
    }

    @Override
    public void onShown(@NotNull AdStatusHandler adStatusHandler) {
        Log.d(SampleActivity.TAG, type.name() + " Ad received Show action");
    }

    @Override
    public void onShowFailed(@NotNull String message) {
        String text = type.name() + " Ad show failed: " + message;
        Log.d(SampleActivity.TAG, text);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClicked() {
        Log.d(SampleActivity.TAG, type.name() + " Ad received Click action");
    }

    @Override
    public void onComplete() {
        if (type != AdType.Rewarded)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
        builder.setTitle(type.name() + " Ad complete");
        builder.setMessage("You have been rewarded");
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onClosed() {
        Log.d(SampleActivity.TAG, type.name() + " Ad received Close action");
    }
}
