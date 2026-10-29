-keeppackagenames com.yandex.mobile.ads
-keepclassmembers class * implements com.yandex.mobile.ads.nativeads.NativeAdEventListener { !private <methods>; }
-keepclassmembers class com.yandex.mobile.ads.* {
    public void onImpression(com.yandex.mobile.ads.common.ImpressionData);
}