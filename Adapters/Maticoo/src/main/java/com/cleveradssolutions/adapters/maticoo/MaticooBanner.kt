package com.cleveradssolutions.adapters.maticoo

import android.annotation.SuppressLint
import android.view.View
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdBase
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationBannerAd
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.maticoo.sdk.ad.banner.BannerAd
import com.maticoo.sdk.ad.banner.BannerAdListener
import com.maticoo.sdk.ad.banner.BannerAdOptions
import com.zmaticoo.sdk.base.common.MaticooIds
import com.zmaticoo.sdk.flow.model.ComponentError

internal class MaticooBanner(
    request: MediationBannerAdRequest,
    localExtras: Map<String, Any>
) : MediationAdBase(), MediationAdLoader, MediationBannerAd {

    private val view: BannerAd

    private val maticooListener = object : BannerAdListener() {
        private var request: MediationBannerAdRequest? = request
        override fun onBannerAdReady(ids: MaticooIds, view: View) {
            this.request?.onSuccess(this@MaticooBanner)
            this.request = null
        }

        override fun onBannerAdFailed(ids: MaticooIds, error: ComponentError) {
            this.request?.onFailure(error.toCASError())
            this.request = null
        }

        override fun onBannerAdClicked(ids: MaticooIds) {
            listener?.onAdClicked(this@MaticooBanner)
        }

        override fun onBannerAdShow(ids: MaticooIds) {
            listener?.onAdImpression(this@MaticooBanner)
        }

        override fun onBannerAdShowFailed(ids: MaticooIds, error: ComponentError) {
            listener?.onAdFailedToShow(this@MaticooBanner, error.toCASError())
        }
    }

    init {
        val builder = BannerAdOptions.Builder(request.unitId)
        builder.setCanCloseAd(false) //Can it be closed, default to false
        //builder.setAutoRefresh(false) //Whether to enable automatic refresh, default to true
        view = BannerAd(request.context, builder.build())
        view.layoutParams = request.createAdLayout()
        view.setLocalExtra(localExtras.toMutableMap()) // Required mutable copy
        view.setAdListener(maticooListener)
        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            view.loadAd(request.bidResponse)
        } else {
            view.loadAd()
        }
    }

    override fun createView(
        request: MediationBannerAdRequest,
        listener: MediationAdListener
    ): View {
        return view
    }

    @SuppressLint("WrongThread")
    override fun destroy() {
        super.destroy()
        view.destroy()
    }
}