package com.cleveradssolutions.adapters.ysonetwork

import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.sdk.base.CASHandler
import com.cleversolutions.ads.AdError
import com.ysocorp.ysonetwork.YsoNetwork
import com.ysocorp.ysonetwork.enums.YNEnumInitializationStatus

internal class YsoInitListener(
    val request: MediationInitAdRequest
) : Runnable {
    override fun run() {
        val status = YsoNetwork.initializationStatus()
        if (status == YNEnumInitializationStatus.Initializing) {
            CASHandler.post(500, this)
            return
        }
        if (status == YNEnumInitializationStatus.Initialized) {
            request.onSuccess()
        } else {
            request.onFailure(AdError.NOT_INITIALIZED)
        }
    }
}