package com.cleveradssolutions.adapters.bidmachine

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.cleveradssolutions.mediation.MediationNativeAdContent
import com.cleveradssolutions.mediation.api.MediationAdListener
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.sdk.nativead.NativeAdAssetViews
import com.cleversolutions.ads.AdError
import io.bidmachine.ImageData
import io.bidmachine.nativead.NativeAd
import io.bidmachine.nativead.NativeListener
import io.bidmachine.nativead.NativeRequest
import io.bidmachine.nativead.view.NativeMediaView
import io.bidmachine.utils.BMError

internal class BMNativeAd(
    request: MediationNativeAdRequest
) : MediationNativeAdContent(), MediationAdLoader, NativeListener {

    private var request: MediationNativeAdRequest? = request
    private val ad = NativeAd(request.context).also { it.setListener(this) }
    private var mediaView: NativeMediaView? = null

    fun load(request: MediationNativeAdRequest) {
        ad.load(request.buildBMNativeRequest().build())
    }

    fun load(adRequest: NativeRequest) {
        ad.load(adRequest)
    }

    override val isExpired: Boolean
        get() = super.isExpired || ad.isExpired

    override fun createMediaContentView(context: Context): View? =
        mediaView ?: NativeMediaView(context).also { mediaView = it }

    override fun createAdChoicesContentView(context: Context): View? =
        ad.getProviderView(context)

    /**
     * BidMachine caches an asset to a local file when it can, and otherwise leaves only
     * the remote address. CAS loads either, so the remote one is passed on as it is.
     */
    private fun getUri(data: ImageData): Uri? =
        data.localUri ?: data.remoteUrl?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }

    override fun onAdLoaded(ad: NativeAd) {
        val request = this.request ?: return
        this.request = null
        creativeId = ad.auctionResult?.creativeId

        headline = ad.title
        body = ad.description
        callToAction = ad.callToAction
        hasVideoContent = ad.hasVideo()
        ad.rating.takeIf { it > 0f }?.let { starRating = it.toDouble() }
        ad.icon?.let {
            icon = it.image
            iconUri = getUri(it)
        }
        ad.mainImage?.let {
            mediaImage = it.image
            mediaImageUri = getUri(it)
        }

        request.onSuccess(this)
    }

    override fun onAdLoadFailed(ad: NativeAd, error: BMError) {
        val request = this.request
        this.request = null
        request?.onFailure(error.toCASError())
    }

    override fun onAdExpired(ad: NativeAd) {
        val request = this.request
        this.request = null
        request?.onFailure(AdError.EXPIRED)
        setAdExpired()
    }

    override fun onRenderedInView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews,
        listener: MediationAdListener
    ) {
        ad.registerView(view, assets.iconView, mediaView, assets.clickableViews.toSet())
    }

    override fun onDetachFromView(
        view: ViewGroup,
        container: ViewGroup,
        assets: NativeAdAssetViews
    ) {
        super.onDetachFromView(view, container, assets)
        ad.unregisterView()
    }

    override fun onAdImpression(ad: NativeAd) {
        listener?.onAdImpression(this)
    }

    override fun onAdShowFailed(ad: NativeAd, error: BMError) {
        listener?.onAdFailedToShow(this, error.toCASError())
    }

    override fun onAdClicked(ad: NativeAd) {
        listener?.onAdClicked(this)
    }

    override fun destroy() {
        super.destroy()
        request = null
        mediaView = null
        ad.destroy()
    }
}