package com.cleveradssolutions.adapters.startio

import android.util.Log
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.android.CAS
import com.startapp.sdk.adsbase.model.AdPreferences
import com.startapp.sdk.adsbase.remoteconfig.MetaData

internal fun MediationAdUnitRequest.createSIOPreferences(): AdPreferences {
    val preferences = AdPreferences()
    if (bidResponse == null && floor > 0.0) {
        preferences.minCpm = floor
    }

    preferences.adTag = unitId
    preferences.placementId = unitLabel

    if (isTestRequest)
        preferences.isTestMode = true

    CAS.targetingOptions.keywords?.let {
        preferences.keywords = it.joinToString(",")
    }
    CAS.targetingOptions.location?.let {
        preferences.latitude = it.latitude
        preferences.longitude = it.longitude
    }
    return preferences
}

internal fun getCASError(message: String?, isBelowFloor: Boolean?): AdError =
    if (isBelowFloor == true)
        AdError(AdErrorCode.NO_FILL, "Below Floor")
    else if (message == null)
        AdError(AdErrorCode.INTERNAL_ERROR)
    else if (message.contains("204") || message.contains("Empty"))
        AdError.NO_FILL
    else if (message == "serving ads disabled")
        AdError(AdErrorCode.CONFIGURATION_ERROR, message)
    else
        AdError(AdErrorCode.INTERNAL_ERROR, message)

internal fun disableAutoRetries() {
    val data: MetaData = MetaData.E() // return k; where k is MetaData
    val stopAutoLoadAmount: Int = data.X() // return this.stopAutoLoadAmount;
    val stopAutoLoadPreCacheAmount: Int = data.Y() // return this.stopAutoLoadPreCacheAmount;

    if (stopAutoLoadAmount > 0)
        setZeroMetaData(data, "stopAutoLoadAmount")
    if (stopAutoLoadPreCacheAmount > 0)
        setZeroMetaData(data, "stopAutoLoadPreCacheAmount")
}

private fun setZeroMetaData(data: MetaData, field: String) {
    try {
        data.javaClass.getDeclaredField(field).let {
            it.isAccessible = true
            it.setInt(data, 0)
            it.isAccessible = false
        }
    } catch (e: Throwable) {
        Log.e("CAS.AI", "$field failed", e)
    }
}