package com.cleveradssolutions.adapters.unity

import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import com.unity3d.ads.InterstitialAd
import com.unity3d.ads.LoadListener
import com.unity3d.ads.RewardedAd
import com.unity3d.ads.UnityAdsError

internal class UnityScreenAdLoader<T>(request: MediationScreenAdRequest) : MediationAdLoader,
    LoadListener<T> {
    private var request: MediationScreenAdRequest? = request

    override fun onAdLoaded(unityAd: T?, error: UnityAdsError?) {
        val request = request ?: return
        this.request = null
        when (unityAd) {
            null -> request.onFailure(mapUnityError(error))
            is InterstitialAd -> request.onSuccess(UnityInterstitialAd(unityAd))
            is RewardedAd -> request.onSuccess(UnityRewardedAd(unityAd))
            else -> request.onFailure(AdError("Loaded not implemented type ${unityAd.javaClass.name}"))
        }
    }
}