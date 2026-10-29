package com.cleveradssolutions.adapters.bluesea

import com.cleveradssolutions.sdk.AdErrorCode
import com.cleversolutions.ads.AdError

// Docs: https://docs.google.com/document/d/1jbPSfvHxVtiwwfBe6oTZDPfuzRjk1oRJjN3Rpe4Am9w/edit?tab=t.0

internal fun com.blueseasx.sdk.AdError.toCASError(): AdError =
    getCASErrorForBSCode(code, message)

internal fun getCASErrorForBSCode(code: Int, message: String?): AdError =
    if (code in 20003..20034)
        AdError(AdErrorCode.NO_FILL, "$message (c$code)")
    else when (code) {
        10000, // SDK failed to initialize due to missing appid
        20000, // Placement id shall not be empty
        20002, // App is inactive, please check your app setup
            -> AdError(AdErrorCode.CONFIGURATION_ERROR, message)

        10001, // SDK failed to initialize due to no context
        10002, // getToken failed because sdk hasn’t been initialized
        10003, // getToken failed because sdk failed to initialize
        10004, // failed to generate token
        20001, // Load failed because sdk hasn't been initialized
            -> AdError(AdErrorCode.NOT_INITIALIZED, message)

        20035 -> // Load failed because of coppa restriction
            AdError(AdErrorCode.REJECTED, message)

        30001, // The ad is expired
        30002, // The ad is destroyed
            -> AdError(AdErrorCode.NOT_READY, message)

        else ->
            AdError(AdErrorCode.INTERNAL_ERROR, "$message (c$code)")
    }
