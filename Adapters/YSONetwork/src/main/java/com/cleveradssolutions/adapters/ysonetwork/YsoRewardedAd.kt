package com.cleveradssolutions.adapters.ysonetwork

import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.ysocorp.ysonetwork.YsoNetwork

internal class YsoRewardedAd(request: MediationRewardedAdRequest) :
    YsoInterstitialAd(request) {

    override fun showScreen(listener: MediationAdListener) {
        val activity = listener.requireUIContext(this) ?: return
        YsoNetwork.rewardedShow(auctionId, this, activity)
    }
}
