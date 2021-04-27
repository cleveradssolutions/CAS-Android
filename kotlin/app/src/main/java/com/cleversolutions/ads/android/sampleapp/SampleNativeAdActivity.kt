package com.cleversolutions.ads.android.sampleapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.nativead.CASNativeLoader
import com.cleversolutions.ads.nativead.CASNativeViewBidder
import com.cleversolutions.ads.nativead.NativeAd
import com.cleversolutions.ads.nativead.mediation.NativeAdListener
import com.cleversolutions.ads.nativead.mediation.NativeLoadListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_native_ad.*

class SampleNativeAdActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_ad)

        // Create native ad with view with default layout
        val bidder = CASNativeViewBidder.Builder(R.layout.view_native_ad)
            .setAdvertiserId(R.id.ad_advertiser)
            .setBodyId(R.id.ad_body)
            .setCallToActionId(R.id.ad_call_to_action)
            .setHeadLineId(R.id.ad_headline)
            .setIconId(R.id.ad_app_icon)
            .setImageId(R.id.ad_app_image)
            .setMediaId(R.id.ad_media)
            .setPriceId(R.id.ad_price)
            .setStarRatingId(R.id.ad_stars)
            .setStoreId(R.id.ad_store)
            .setAdChoiceId(R.id.ad_adChoice)
            .build()

        val loader = CASNativeLoader.Builder(CAS.manager!!)
            .setAdChoice(CASNativeLoader.Builder.ADCHOICES_TOP_RIGHT)
            .withLoadListener( object :
                NativeLoadListener {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    // Attach nativeAd View
                    container_ad.addView(
                        nativeAd.adView,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ))

                    Log.d(SampleActivity.TAG, "${AdType.Native} loaded")
                }

                override fun onNativeAdFailedToLoad(error: AdError) {
                    Log.d(SampleActivity.TAG, "${AdType.Native} failed to load ${error.message}")
                }
            })
            .withAdListener( object : NativeAdListener {
                override fun onAdClicked() {
                    Log.d(SampleActivity.TAG, "${AdType.Native} clicked")
                }

                override fun onAdOpened() {
                    Log.d(SampleActivity.TAG, "${AdType.Native} opened")
                }
            })
            .build()

        loader.loadAd(bidder)

        backButtonActivity.setOnClickListener {
            val intent = Intent(this, SampleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
}