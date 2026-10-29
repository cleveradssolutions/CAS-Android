package com.cleveradssolutions.adapters.pubmatic

import com.cleveradssolutions.mediation.core.MediationAd
import com.pubmatic.sdk.common.POBError
import com.pubmatic.sdk.openwrap.core.POBReward
import com.pubmatic.sdk.rewardedad.POBRewardedAd

internal class RewardedListener(
    private var loader: PubMaticBidLoader?
) : POBRewardedAd.POBRewardedAdListener() {
    private var loadedAd: MediationAd? = null

    override fun onAdReceived(ad: POBRewardedAd) {
        val loader = loader ?: return
        this.loader = null
        val request = loader.request?.forScreenAd() ?: return

        val casAd = PubMaticAd(ad)
        loader.fillAdContent(casAd)
        loadedAd = casAd
        request.onSuccess(casAd)
    }

    override fun onAdFailedToLoad(ad: POBRewardedAd, error: POBError) {
        loader?.onBidFailed(ad, error)
        loader = null
    }

    override fun onAdFailedToShow(ad: POBRewardedAd, error: POBError) {
        loadedAd?.let {
            it.listener?.onAdFailedToShow(it, error.toCASError())
        }
    }

    override fun onAdExpired(p0: POBRewardedAd) {
        loadedAd?.let {
            it.listener?.onAdExpired(it)
        }
    }

    override fun onAdOpened(ad: POBRewardedAd) {
        loadedAd?.let {
            it.listener?.onAdShowed(it)
        }
    }

    override fun onAdImpression(p0: POBRewardedAd) {
        loadedAd?.let {
            it.listener?.onAdImpression(it)
        }
    }

    override fun onAdClicked(ad: POBRewardedAd) {
        loadedAd?.let {
            it.listener?.onAdClicked(it)
        }
    }

    override fun onAdClosed(ad: POBRewardedAd) {
        loadedAd?.let {
            it.listener?.onAdDismissed(it)
        }
    }

    override fun onReceiveReward(ad: POBRewardedAd, reward: POBReward) {
        loadedAd?.let {
            it.listener?.onUserEarnedReward(it)
        }
    }
}


