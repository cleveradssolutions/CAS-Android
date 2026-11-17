package com.cleveradssolutions.adapters.bidmachine

import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import io.bidmachine.AdContentType
import io.bidmachine.AdFormat
import io.bidmachine.AdPlacementConfig
import io.bidmachine.BannerAdSize
import io.bidmachine.CustomParams
import io.bidmachine.MediaAssetType
import io.bidmachine.PriceFloorParams
import io.bidmachine.TargetingParams
import io.bidmachine.banner.BannerRequest
import io.bidmachine.interstitial.InterstitialRequest
import io.bidmachine.models.RequestBuilder
import io.bidmachine.nativead.NativeRequest
import io.bidmachine.rewarded.RewardedRequest
import io.bidmachine.utils.BMError

internal fun BMError.toCASError(): AdError = when (code) {
    BMError.NO_CONNECTION -> AdError.NO_CONNECTION
    BMError.NO_CONTENT -> AdError.NO_FILL
    BMError.EXPIRED -> AdError.EXPIRED
    BMError.DESTROYED, BMError.DUPLICATED -> AdError.NOT_READY
    else -> AdError("$message (c$code)")
}

/**
 * The key of the ad unit parameter that carries [MEDIATION_MODE] to BidMachine.
 */
private const val MEDIATION_MODE = "mediation_mode"

/**
 * The placement id is optional for BidMachine, and a real one is usually 74 characters long.
 * CAS always requires a unit id, so the field can hold any non-empty value that is not
 * a BidMachine placement id. Only a value long enough to be a placement id is passed on.
 *
 * [MEDIATION_MODE] tells BidMachine which kind of integration the request comes
 * from, so its auction can adapt: "prebid" for a round that runs before the other
 * networks, "bidding" for a round that runs with them, "postbid" for a call after
 * the main auction with the winner's eCPM as the floor. The value is passed
 * through as it is configured; an unset parameter sends no custom params at all.
 */
private fun buildAdPlacement(request: MediationAdUnitRequest, format: AdFormat) =
    with(AdPlacementConfig.Builder(format)) {
        if (request.unitId.length > 70) {
            withPlacementId(request.unitId)
        }
        request.getStrParameter(MEDIATION_MODE)?.let {
            withCustomParams(CustomParams().addParam(MEDIATION_MODE, it))
        }
        build()
    }

/**
 * The publisher asks for the largest banner they can place and BidMachine fills it
 * with the best ad that fits, never with a larger one.
 *
 * BidMachine supports adaptive sizes but not inline ones, so an inline request is
 * served as adaptive, except for the medium rectangle, which has a fixed size of
 * its own. A thin banner never gets here, the adapter leaves it to the SDK.
 */
internal fun MediationBannerAdRequest.buildBidMachineBannerPlacement(): AdPlacementConfig {
    val size = adSize
    val bidSize = if (size.isAdaptive || (size.isInline && adSizeId != AdSizeId.MREC)) {
        BannerAdSize.adaptive(size.width, size.height)
    } else when (adSizeId) {
        AdSizeId.LARGE -> BannerAdSize.Leaderboard
        AdSizeId.MREC -> BannerAdSize.MediumRectangle
        else -> BannerAdSize.Banner
    }
    return buildAdPlacement(this, AdFormat.createBanner(bidSize))
}

internal fun MediationAdUnitRequest.buildBidMachineInterstitialPlacement() =
    buildAdPlacement(this, AdFormat.createInterstitial(AdContentType.All))

internal fun MediationAdUnitRequest.buildBidMachineRewardedPlacement() =
    buildAdPlacement(this, AdFormat.createRewarded(AdContentType.All))

internal fun MediationAdUnitRequest.buildBidMachineNativePlacement() =
    buildAdPlacement(this, AdFormat.createNative(MediaAssetType.DEFAULT))

internal fun MediationBannerAdRequest.buildBidMachineBannerRequest() =
    BannerRequest.Builder(buildBidMachineBannerPlacement()).setCASParameters(this)

internal fun MediationScreenAdRequest.buildBidMachineInterstitialRequest() =
    InterstitialRequest.Builder(buildBidMachineInterstitialPlacement()).setCASParameters(this)

internal fun MediationScreenAdRequest.buildBidMachineRewardedRequest() =
    RewardedRequest.Builder(buildBidMachineRewardedPlacement()).setCASParameters(this)

internal fun MediationNativeAdRequest.buildBidMachineNativeRequest() =
    NativeRequest.Builder(buildBidMachineNativePlacement()).setCASParameters(this)

/**
 * A Server Bidding request carries the markup of an auction that already ran on our
 * server, so BidMachine renders it as is. The other flows let BidMachine run its own
 * auction, bounded by the floor of the request.
 */
internal fun <T : RequestBuilder<*, *>> T.setCASParameters(request: MediationAdUnitRequest): T {
    val bidResponse = request.bidResponse
    if (bidResponse != null) {
        setBidPayload(bidResponse)
    } else if (request.floor > 0.0) {
        setPriceFloorParams(PriceFloorParams().addPriceFloor(request.floor))
    }

    buildTargeting(request)?.let {
        setTargetingParams(it)
    }
    return this
}

private fun buildTargeting(request: MediationAdUnitRequest): TargetingParams? {
    val targeting = request.targeting
    val result = TargetingParams()
    var used = false
    targeting.appStoreCategory?.let {
        used = true
        result.setStoreCategory(it)
    }
    targeting.appStoreUrl?.let {
        used = true
        result.setStoreUrl(it)
    }
    targeting.blockedIABCategory?.forEach {
        used = true
        result.addBlockedAdvertiserIABCategory(it)
    }
    targeting.blockedAdDomain?.forEach {
        used = true
        result.addBlockedAdvertiserDomain(it)
    }
    targeting.blockedAdApps?.forEach {
        used = true
        result.addBlockedApplication(it)
    }
    return if (used) result else null
}