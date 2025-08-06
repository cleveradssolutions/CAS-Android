## CAS.AI Mediation adapters

All adapters for supported networks are found in the `com.cleveradssolutions` dependency group, and do not require additional SDK dependencies to be added.  

|        Adapter         |       Supported Formats        | Bidding | Changelog | SDK Versions |
|:----------------------:|:------------------------------:|:-------:| :-------: | :----------: |
| `:ironsource:8.10.0.0` |  Banner, Interstitial, Rewarded | Yes | [view](IronSource/CHANGELOG.md) | [link](https://developers.ironsrc.com/ironsource-mobile/android/sdk-change-log/) |
| `:google:24.5.0.0` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](GoogleMobileAds/CHANGELOG.md) | [link](https://developers.google.com/admob/android/rel-notes) |
| `:unity:4.16.0.0` |  Banner, Interstitial, Rewarded | Yes | [view](UnityAds/CHANGELOG.md) | [link](https://docs.unity.com/ads/en-us/manual/Changelog) |
| `:chartboost:9.9.2.0` |  Banner, Interstitial, Rewarded | Yes | [view](Chartboost/CHANGELOG.md) | [link](https://docs.chartboost.com/en/monetization/integrate/android/changelog/) |
| `:vungle:7.5.0.0` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](LiftoffMonetize/CHANGELOG.md) | [link](https://support.vungle.com/hc/en-us/articles/15722228922395-Download-Vungle-SDK-for-Android-Amazon) |
| `:prado:10.0.4.0` |  Banner, Interstitial, Rewarded | No | [view](Prado/CHANGELOG.md) | [link](https://github.com/Prado-SDK/prado-mobile-sdk/tree/release/10.0.0/Prado%20Direct/Android) |
| `:mintegral:16.9.91.0` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](Mintegral/CHANGELOG.md) | [link](https://dev.mintegral.com/doc/index.html?file=sdk-m_sdk-android&lang=en) |
| `:kidoz:10.0.4.0` |  Banner, Interstitial, Rewarded | No | [view](Kidoz/CHANGELOG.md) | [link](https://github.com/Kidoz-SDK/kidoz-mobile-sdk/tree/release/10.0.0/Kidoz%20Direct/Android) |
| `:fyber:8.3.8.0` |  Banner, Interstitial, Rewarded | Yes | [view](DTExchange/CHANGELOG.md) | [link](https://developer.digitalturbine.com/hc/en-us/articles/360010834177-DT-Exchange-Android-Changelog) |
| `:inmobi:10.8.7.0` |  Banner, Interstitial, Rewarded, Native | Yes | [view](InMobi/CHANGELOG.md) | [link](https://support.inmobi.com/monetize/sdk-documentation/android-guidelines/changelogs-android/android-changelogs-kotlin) |
| `:applovin:13.3.1.1` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](AppLovin/CHANGELOG.md) | [link](https://github.com/AppLovin/AppLovin-MAX-SDK-Android/releases) |
| `:pangle:7.3.0.5` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](Pangle/CHANGELOG.md) | [link](https://www.pangleglobal.com/publisher/integration) |
| `:bigo:5.5.0.0` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](BigoAds/CHANGELOG.md) | [link](https://www.bigossp.com/guide/sdk/android/version) |
| `:yandex:27.15.0.0` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](YandexMobileAds/CHANGELOG.md) | [link](https://github.com/yandexmobile/yandex-ads-sdk-android/blob/master/changelogs/mobileads/CHANGELOG.md) |
| `:facebook:6.20.0.0` |  Banner, Interstitial, Rewarded, Native | Yes | [view](AudienceNetwork/CHANGELOG.md) | [link](https://developers.facebook.com/docs/audience-network/setting-up/platform-setup/android/changelog) |
| `:ysonetwork:1.2.9.1` |  Banner, Interstitial, Rewarded | Yes | [view](YsoNetwork/CHANGELOG.md) | [link](null) |
| `:cas-exchange:4.3.0` |  Banner, Interstitial, Rewarded | Yes | [view](CASExchange/CHANGELOG.md) | [link](null) |
| `:cas-promo:4.0.2` |  Banner, Interstitial, Rewarded | Yes | [view](CrossPromo/CHANGELOG.md) | [link](null) |
| `:hyprmx:6.4.3.0` |  Banner, Interstitial, Rewarded | No | [view](HyprMX/CHANGELOG.md) | [link](https://documentation.hyprmx.com/android-sdk/downloads-change-log/change-log/android-sdk-change-log) |
| `:startio:5.2.3.1` |  Banner, Interstitial, Rewarded, AppOpen, Native | No | [view](StartIO/CHANGELOG.md) | [link](https://support.start.io/hc/en-us/articles/5813405015442-Android-SDK-Change-Log) |
| `:maticoo:1.8.6.1` |  Banner, Interstitial, Rewarded, AppOpen, Native | Yes | [view](Maticoo/CHANGELOG.md) | [link](https://www.yuque.com/maticoo/ivzbqi/qoothshaokc75cl7) |
| `:madex:1.7.5.0` |  Banner, Interstitial, Rewarded | No | [view](Madex/CHANGELOG.md) | [link](https://madex.gitbook.io/madex-documentation/android-sdk/change-log) |
| `:ogury:6.1.0.0` |  Banner, Interstitial, Rewarded | Yes | [view](Ogury/CHANGELOG.md) | [link](https://support.ogury.com/inapp/release-notes/ogury-sdk/android) |
| `:smaato:22.7.2.0` |  Banner, Interstitial, Rewarded | Yes | [view](Smaato/CHANGELOG.md) | [link](https://developers.smaato.com/publishers/nextgen-sdk-android-changelog/) |


## Adapter versioning
The adapter versioning scheme for versioned adapters is `<third-party SDK version>.<adapter patch version>`. For example, if an ad network releases a new SDK version `1.2.3`, a new adapter version `1.2.3.0` will be released to Bintray after being tested against that new SDK.

If an adapter needs updating outside the lifecycle of a third-party SDK release, the patch version will increase. A bug fix for adapter version `1.2.3.0` will be released in version `1.2.3.1`.

## CAS compatible versions
When integrating the CAS Gradle Plugin, compatible adapter versions are selected automatically. To change the version of a specific adapter, set false in the list of adapters for the CAS Gradle Plugin and manually add the dependency for the version you need.

```kotlin
dependencies {
    implementation("com.cleveradssolutions:google:+")
}
```