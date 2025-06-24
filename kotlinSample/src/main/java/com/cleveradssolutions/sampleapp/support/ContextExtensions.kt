package com.cleveradssolutions.sampleapp.support

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.cleveradssolutions.sampleapp.SampleApplication.Companion.TAG

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
    Log.d(TAG, message)
}

fun Context.toastError(error: String, length: Int = Toast.LENGTH_SHORT) {
    val message = "Error: $error"
    Toast.makeText(this, message, length).show()
    Log.e(TAG, message)
}