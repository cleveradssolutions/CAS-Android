package com.cleveradssolutions.adapters

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import com.brandio.ads.Controller
import com.brandio.ads.DioInterstitialActivity
import com.brandio.ads.exceptions.DIOError
import com.brandio.ads.exceptions.DioSdkException
import com.brandio.ads.listeners.SdkInitListener
import com.brandio.ads.placements.BannerPlacement
import com.brandio.ads.placements.InfeedPlacement
import com.brandio.ads.placements.InterstitialPlacement
import com.brandio.ads.placements.MediumRectanglePlacement
import com.brandio.ads.placements.NativePlacement
import com.brandio.ads.placements.Placement
import com.cleveradssolutions.adapters.displayio.BuildConfig
import com.cleveradssolutions.adapters.displayio.DisplayIOLoader
import com.cleveradssolutions.adapters.displayio.toCASError
import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.internal.mediation.AdapterSupport
import com.cleveradssolutions.mediation.core.MediationAdLoader
import com.cleveradssolutions.mediation.core.MediationAdSignalRequest
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationAdapterBase
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationInitAdRequest
import com.cleveradssolutions.mediation.core.MediationInterstitialAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationRewardedAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError

class DisplayIOAdapter : MediationAdapterBase(), SdkInitListener {

    override fun getSDKVersion(): String = Controller.getInstance().ver
    override fun getMinSDKVersion(): String = BuildConfig.VERSION_SDK
    override fun getAdapterVersion(): String = BuildConfig.VERSION
    override fun getActivityClass(): Class<*> = DioInterstitialActivity::class.java

    override fun supportBidding(): Int = AdapterSupport.simple or AdapterSupport.nativ

    override val isInitialized: Boolean
        get() = Controller.getInstance().isInitialized

    override fun initAds(request: MediationInitAdRequest) {
        val appId = request.requireAppID() ?: return
        Controller.getInstance().init(request.context, appId, request.targeting.userId, this)
    }

    override fun onInit() {
        initRequest.onSuccess()
    }

    override fun onInitError(error: DIOError) {
        initRequest.onFailure(error.toCASError())
    }

    @SuppressLint("ThreadConstraint")
    @MainThread
    override fun loadAd(request: MediationBannerAdRequest): MediationAdLoader? {
        if(request.adSizeId == AdSizeId.THIN){
            return super.loadAd(request)
        }
        val bidResponse = request.bidResponse
        val placement = requirePlacement(request) ?: return null
        val loader = DisplayIOLoader(request = request, placement = placement)
        when (placement) {
            is BannerPlacement -> {
                if (bidResponse != null) {
                    placement.loadBannerFromORTB(bidResponse, loader)
                } else {
                    loader.loadWithFloor(request)
                }
            }

            is MediumRectanglePlacement -> {
                if (bidResponse != null) {
                    placement.loadMRECTFromORTB(bidResponse, loader)
                } else {
                    loader.loadWithFloor(request)
                }
            }

            is InfeedPlacement -> {
                if (bidResponse != null) {
                    placement.loadInfeedFromORTB(bidResponse, loader)
                } else {
                    loader.loadWithFloor(request)
                }
            }

            else -> {
                request.onFailure(
                    AdError(
                        AdErrorCode.CONFIGURATION_ERROR,
                        "Invalid format: ${placement::class.java.simpleName}"
                    )
                )
                return null
            }
        }

        return loader
    }

    override fun loadAd(request: MediationNativeAdRequest): MediationAdLoader? {
        val placement = requirePlacement(request) ?: return null
        if (placement !is NativePlacement) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Invalid format: ${placement::class.java.simpleName}"
                )
            )
            return null
        }

        val loader = DisplayIOLoader(request, placement)
        val bidResponse = request.bidResponse
        if (bidResponse != null) {
            placement.loadNativeFromORTB(bidResponse, loader)
        } else {
            loader.loadWithFloor(request)
        }

        return loader
    }

    override fun loadAd(request: MediationInterstitialAdRequest): MediationAdLoader? =
        loadScreenAd(request)

    override fun loadAd(request: MediationRewardedAdRequest): MediationAdLoader? =
        loadScreenAd(request)

    private fun loadScreenAd(request: MediationScreenAdRequest): MediationAdLoader? {
        val placement = requirePlacement(request) ?: return null
        if (placement !is InterstitialPlacement) {
            request.onFailure(
                AdError(
                    AdErrorCode.CONFIGURATION_ERROR,
                    "Invalid format: ${placement::class.java.simpleName}"
                )
            )
            return null
        }

        val bidResponse = request.bidResponse
        val loader = DisplayIOLoader(request, placement)
        if (bidResponse != null) {
            placement.loadInterstitialFromORTB(bidResponse, loader)
        } else {
            loader.loadWithFloor(request)
        }
        return loader
    }

    override fun collectSignals(request: MediationAdSignalRequest) {
        val token = Controller.getInstance().token
        if (token.isNullOrBlank()) {
            request.onFailure(AdError(AdErrorCode.REJECTED))
            return
        }

        request.onSuccess(token)
    }

    private fun requirePlacement(request: MediationAdUnitRequest): Placement? =
        try {
            Controller.getInstance().getPlacement(request.unitId)
        } catch (e: DioSdkException) {
            request.onFailure(AdError(AdErrorCode.CONFIGURATION_ERROR, e.localizedMessage))
            null
        } catch (e: Throwable) {
            request.onFailure(AdError(e.localizedMessage))
            null
        }
}