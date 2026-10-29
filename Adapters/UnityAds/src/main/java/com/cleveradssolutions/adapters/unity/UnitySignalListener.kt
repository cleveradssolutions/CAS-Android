package com.cleveradssolutions.adapters.unity

import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.unity3d.ads.IUnityAdsTokenListener

internal class UnitySignalListener(val request: MediationAdSignalRequest) : IUnityAdsTokenListener {
    override fun onUnityAdsTokenReady(token: String?) {
        request.onSuccess(token.orEmpty())
    }
}