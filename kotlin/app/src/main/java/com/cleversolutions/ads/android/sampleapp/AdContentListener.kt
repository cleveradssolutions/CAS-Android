package com.cleversolutions.ads.android.sampleapp

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.cleversolutions.ads.AdCallback
import com.cleversolutions.ads.AdStatusHandler
import com.cleversolutions.ads.AdType

class AdContentListener(private val context: Context, private val type: AdType) : AdCallback {

    override fun onShown(ad: AdStatusHandler) {
        Log.d(SampleActivity.TAG, "$type Ad received Show action")
    }

    override fun onShowFailed(message: String) {
        val text = "$type Ad show failed: $message"
        Log.d(SampleActivity.TAG, text)
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onClicked() {
        Log.d(SampleActivity.TAG, "$type Ad received Click action")
    }

    override fun onComplete() {
        if (type != AdType.Rewarded)
            return
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
        builder.setTitle("$type Ad complete")
        builder.setMessage("You have been rewarded")
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onClosed() {
        Log.d(SampleActivity.TAG, "$type Ad received Close action")
    }
}