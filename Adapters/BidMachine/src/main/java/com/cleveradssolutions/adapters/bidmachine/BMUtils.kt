package com.cleveradssolutions.adapters.bidmachine

import com.cleveradssolutions.internal.AdSizeId
import com.cleveradssolutions.mediation.core.MediationAdUnitRequest
import com.cleveradssolutions.mediation.core.MediationBannerAdRequest
import com.cleveradssolutions.mediation.core.MediationNativeAdRequest
import com.cleveradssolutions.mediation.core.MediationScreenAdRequest
import com.cleversolutions.ads.AdError
import io.bidmachine.AdPlacementConfig
import io.bidmachine.BannerAdSize
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
    BMError.NO_CONNECTION, BMError.TIMEOUT -> AdError.NO_CONNECTION
    BMError.NO_CONTENT -> AdError.NO_FILL
    BMError.EXPIRED -> AdError.EXPIRED
    BMError.DESTROYED, BMError.DUPLICATED -> AdError.NOT_READY
    else -> AdError("$message (c$code)")
}

/**
 * The publisher asks for the largest banner they can place and BidMachine fills it
 * with the best ad that fits, never with a larger one.
 *
 * BidMachine supports adaptive sizes but not inline ones, so an inline request is
 * served as adaptive, except for the medium rectangle, which has a fixed size of
 * its own. A thin banner never gets here, the adapter leaves it to the SDK.
 */
internal fun MediationBannerAdRequest.bannerSize(): BannerAdSize {
    val size = adSize
    if (size.isAdaptive || (size.isInline && adSizeId != AdSizeId.MREC)) {
        return BannerAdSize.adaptive(size.width, size.height)
    }
    return when (adSizeId) {
        AdSizeId.LARGE -> BannerAdSize.Leaderboard
        AdSizeId.MREC -> BannerAdSize.MediumRectangle
        else -> BannerAdSize.Banner
    }
}

internal fun MediationBannerAdRequest.buildBMBannerRequest(): BannerRequest.Builder =
    BannerRequest.Builder(
        AdPlacementConfig.bannerBuilder(bannerSize())
            .setPlacementIdFromCAS(this)
            .build()
    ).setCASParameters(this)

internal fun MediationScreenAdRequest.buildBMInterstitialRequest() = InterstitialRequest.Builder(
    AdPlacementConfig.interstitialBuilder()
        .setPlacementIdFromCAS(this)
        .build()
).setCASParameters(this)

internal fun MediationScreenAdRequest.buildBMRewardedRequest() = RewardedRequest.Builder(
    AdPlacementConfig.rewardedBuilder()
        .setPlacementIdFromCAS(this)
        .build()
).setCASParameters(this)

internal fun MediationNativeAdRequest.buildBMNativeRequest() = NativeRequest.Builder(
    AdPlacementConfig.nativeBuilder(MediaAssetType.DEFAULT)
        .setPlacementIdFromCAS(this)
        .build()
).setCASParameters(this)


/**
 * The placement is optional for BidMachine: a source with no placements of its own
 * serves ads on the source id alone, and a blank placement is not a valid value.
 */
internal fun AdPlacementConfig.Builder.setPlacementIdFromCAS(
    request: MediationAdUnitRequest
): AdPlacementConfig.Builder =
    if (request.unitId.isNotEmpty()) withPlacementId(request.unitId)
    else this

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