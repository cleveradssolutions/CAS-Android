package com.cleveradssolutions.adapters.ysonetwork

import android.view.View
import com.cleveradssolutions.internal.Constants
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleversolutions.ads.AdError
import com.ysocorp.ysonetwork.YsoNetwork
import com.ysocorp.ysonetwork.enums.YNEnumActionError

internal class YsoBannerAd(request: MediationBannerAdRequest) :
    MediationAdBase(), MediationBannerAd, MediationAdLoader,
    YsoNetwork.ActionLoad, YsoNetwork.ActionDisplay {
    private var request: MediationBannerAdRequest? = request
    private var view: View? = null
    val auctionId = request.getStrParameter(Constants.KEY_BID_AUCTION_ID)
        ?: request.unitId

    init {
        // Unit Id required to continue load banner
        unitId = request.unitId
    }

    override fun onLoad(error: YNEnumActionError?) {
        val request = request ?: return
        if (error != YNEnumActionError.None) {
            this.request = null
            request.onFailure(casErrorFromYso(error))
            return
        }
        // YSO banner have two stages of ad loading:
        // 1. bannerLoad
        // 2. bannerShow to get Ad View
        val activity = request.requireUIContext() ?: return
        YsoNetwork.bannerShow(auctionId, this, activity)
    }

    override fun onDisplay(view: View?) {
        val request = request ?: return
        this.request = null
        this.view = view
        request.forBannerAd().onSuccess(this)
    }

    override fun onClick() {
        listener?.onAdClicked(this)
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        val view = view!!
        // Loaded view have layout params MACH_PARENT
        view.layoutParams = request.createAdaptiveAdLayout()
        overrideImpressionHandling(view)
        return view
    }

    override fun onClose(display: Boolean, complete: Boolean) {
        if (!display) {
            request?.let {
                it.onFailure(AdError.NOT_READY)
                request = null
                return
            }
            listener?.onAdFailedToShow(this, AdError.NOT_READY)
        }
    }
}
