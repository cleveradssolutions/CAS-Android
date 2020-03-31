
# Chartboost
-keep class com.chartboost.** { *; }

# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Iron Source
-keepclassmembers class com.ironsource.sdk.controller.IronSourceWebView$JSInterface {
    public *;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep public class com.google.android.gms.ads.** {
   public *;
}
-keep class com.ironsource.adapters.** { *;
}
-dontwarn com.ironsource.mediationsdk.**
-dontwarn com.ironsource.adapters.**
-dontwarn com.moat.**
-keep class com.moat.** { public protected private *; }

-keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
}

# InMobi
-keepattributes SourceFile,LineNumberTable
-keep class com.inmobi.** { *; }
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.picasso.**
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient{
     public *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info{
     public *;
}
# skip the Picasso library classes
-keep class com.squareup.picasso.** {*;}
-dontwarn com.squareup.okhttp.**
# IAB classes
-keep class com.iab.** {*;}
-dontwarn com.iab.**

# Vungle
-keep class com.vungle.warren.** { *; }
-keep class com.vungle.warren.downloader.DownloadRequest
-dontwarn com.vungle.warren.error.VungleError$ErrorCode
-dontwarn com.vungle.warren.downloader.DownloadRequest$Status
-keepclassmembers enum com.vungle.warren.** { *; }
# Moat SDK
-keep class com.moat.** { *; }
-dontwarn com.moat.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-keepattributes *Annotation*
# Retrofit
-keepattributes Signature, InnerClasses
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Okio+OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-keepclassmembers class * extends com.vungle.warren.persistence.Memorable {
   public <init>(byte[]);
}

# UnityAds
# Keep all classes in Unity Ads package
-keep class com.unity3d.ads.** {
   *;
}
# Keep all classes in Unity Services package
-keep class com.unity3d.services.** {
   *;
}
-dontwarn com.google.ar.core.**

# Kidoz
-verbose
-optimizationpasses 5
-dontusemixedcaseclassnames
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn android.support.**
-keepattributes *Annotation*
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class com.kidoz.sdk.api.**{
   *;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
 }

-keepclassmembers class * {
     @android.webkit.JavascriptInterface <methods>;
 }
-keepattributes JavascriptInterface

-dontwarn okio.**
-keep class com.squareup.okhttp3.** {*;}
-dontwarn com.squareup.okhttp3.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}
-keep class pl.droidsonroids.gif.GifInfoHandle{<init>(long,int,int,int);}

-dontwarn android.webkit.**
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keep public class android.webkit.WebView

# StartApp
-keep class com.startapp.** { *; }
-keep class com.truenet.** { *; }

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-dontwarn android.webkit.JavascriptInterface
-dontwarn com.startapp.**
-dontwarn org.jetbrains.annotations.**