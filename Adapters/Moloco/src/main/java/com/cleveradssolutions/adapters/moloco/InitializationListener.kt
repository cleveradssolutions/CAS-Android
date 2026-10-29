package com.cleveradssolutions.adapters.moloco

import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError
import com.moloco.sdk.publisher.Initialization
import com.moloco.sdk.publisher.MolocoInitStatus
import com.moloco.sdk.publisher.MolocoInitializationListener

internal class InitializationListener(
    private val request: MediationInitAdRequest
) : MolocoInitializationListener {

    override fun onMolocoInitializationStatus(initStatus: MolocoInitStatus) {
        if (initStatus.initialization == Initialization.SUCCESS) {
            request.onSuccess()
        } else {
            request.onFailure(
                AdError(
                    AdErrorCode.NOT_INITIALIZED,
                    initStatus.description
                )
            )
        }
    }
}