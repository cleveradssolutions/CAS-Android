-keeppackagenames net.pubnative
# Verve SDK have no consumer-rules and require keep all
# https://github.com/pubnative/pubnative-hybid-android-sdk/wiki/HyBid-Configuration#proguard
-keepattributes Signature
-keep class net.pubnative.** { *; }
-keep class com.iab.omid.library.pubnativenet.** { *; }
