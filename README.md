# CleverAdsSolutions-Android SDK Integration 
[![Bintray](https://img.shields.io/bintray/v/cleveradssolutions/CAS-Android/cas-sdk?label=SDK)](https://bintray.com/cleveradssolutions/CAS-Android)
[![Bintray](https://img.shields.io/bintray/v/cleveradssolutions/CAS-Android/cas-promo?label=Promo)](https://bintray.com/cleveradssolutions/CAS-Android)
[![Bintray](https://img.shields.io/bintray/v/cleveradssolutions/CAS-Android/mediation-teen?label=Mediation%20Teen)](https://bintray.com/cleveradssolutions/CAS-Android)
[![App-ads.txt](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/cleveradssolutions/App-ads.txt/master/Shield.json)](https://github.com/cleveradssolutions/App-ads.txt)

### Before You Start
We support Android Operating Systems Version 4.4 (API level 19) and up.  

# Table of contents
 1.  [Add the CAS SDK to Your Project](#step-1-add-the-cas-sdk-to-your-project)  
 2.  [Add Cross Promotion SDK (Optional)](#step-2-add-cross-promotion-sdk)  
 3.  [Gradle settings](#step-3-gradle-settings)  
 4.  [Update AndroidManifest](#step-4-update-androidmanifest)  
 5.  [Add the CAS default settings file (Optional)](#step-5-add-the-cas-default-settings-file)  
 6.  [Import the CAS SDK](#step-6-import-the-cas-sdk)  
 7.  [Privacy Laws](#step-6-privacy-laws)  
 7.1.  [GDPR Managing Consent](#gdpr-managing-consent)  
 7.2.  [CCPA Compliance](#ccpa-compliance)  
 7.3.  [COPPA and EEA Compliance](#coppa-and-eea-compliance)  
 8.  [Configuring CAS SDK](#step-8-configuring-cas-sdk)
 9.  [Initialize the SDK](#step-9-initialize-the-sdk)  
 10.  [Implement our Ad Units](#step-10-implement-our-ad-units)  
 10.1.  [Banner Ad](#banner-ad)  
 10.2.  [Ad Callback](#adcallback)  
 10.3.  [Check Ad Availability](#check-ad-availability)  
 10.4.  [Show Interstitial Ad](#show-interstitial-ad)  
 10.5.  [Show Rewarded Video Ad](#show-rewarded-video-ad)  
 11.  [Mediation extras](#mediation-extras)
 12.  [GitHub issue tracker](#github-issue-tracker)
 13.  [Support](#support)  
 14.  [License](#license)

## Step 1 Add the CAS SDK to Your Project
<details><summary><b>Add repositories to resolve CAS dependencies</b></summary>

Add the following to your app’s **build.gradle** file inside repositories section:
```gradle
repositories {
      google()
      jcenter()
      maven { url "https://jitpack.io" }
      maven { url "https://dl.bintray.com/cleveradssolutions/CAS-Android" }
      maven { url "https://adcolony.bintray.com/AdColony" }
      maven { url "https://dl.bintray.com/ironsource-mobile/android-sdk" }
      maven { url "https://chartboostmobile.bintray.com/Chartboost" }
      maven { url 'http://dl.bintray.com/gabrielcoman/maven' }
      maven { url "http://dl.bintray.com/superawesome/SuperAwesomeSDK" }
      ...
}
```
***
</details>
<details><summary><b>Simple integration (Option 1)</b></summary>
 
Add one of the following solution to the dependencies section to your application.
 
1. Option. General solution of all certified mediation networks in the [Families Ads program](https://support.google.com/googleplay/android-developer/answer/9283445):  
<details><summary>Google Ads, Unity Ads, IronsSource, AdColony, Kidoz, Vungle, AppLovin, StartApp, InMobi, Chartboost, SuperAwesome</summary>

- Google Ads - [Home](https://admob.google.com/home) - [Privacy Policy](https://policies.google.com/technologies/ads)
- Unity Ads - [Home](https://unity.com/solutions/unity-ads) - [Privacy Policy](https://unity3d.com/legal/privacy-policy)
- IronSource - [Home](https://www.ironsrc.com) - [Privacy Policy](https://developers.ironsrc.com/ironsource-mobile/air/ironsource-mobile-privacy-policy/)
- AdColony - [Home](https://www.adcolony.com) - [Privacy Policy](https://www.adcolony.com/privacy-policy/)
- Kidoz - [Home](https://kidoz.net) - [Privacy Policy](https://kidoz.net/privacy-policy/)
- Vungle - [Home](https://vungle.com) - [Privacy Policy](https://vungle.com/privacy/)
- AppLovin - [Home](https://www.applovin.com) - [Privacy Policy](https://www.applovin.com/privacy/)
- StartApp - [Home](https://www.startapp.com) - [Privacy Policy](https://www.startapp.com/policy/privacy-policy/)
- InMobi - [Home](https://www.inmobi.com) - [Privacy Policy](https://www.inmobi.com/privacy-policy/)
- Chartboost - [Home](https://www.chartboost.com) - [Privacy Policy](https://answers.chartboost.com/en-us/articles/200780269)
- SuperAwesome - [Home](https://www.superawesome.com) - [Privacy Policy](https://www.superawesome.com/privacy-hub/privacy-policy/)   
</details>

```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk-general:1.9.2+' 
}
```

2. Option. General solution of mediation networks that are always recommended to be used:  
<details><summary>Google Ads, Vungle, AdColony, Kidoz, IronsSource, AppLovin, Unity Ads, StartApp, InMobi, Chartboost, Facebook AN, Yandex Ads</summary>

- Google Ads - [Home](https://admob.google.com/home) - [Privacy Policy](https://policies.google.com/technologies/ads)
- Unity Ads - [Home](https://unity.com/solutions/unity-ads) - [Privacy Policy](https://unity3d.com/legal/privacy-policy)
- IronSource - [Home](https://www.ironsrc.com) - [Privacy Policy](https://developers.ironsrc.com/ironsource-mobile/air/ironsource-mobile-privacy-policy/)
- AdColony - [Home](https://www.adcolony.com) - [Privacy Policy](https://www.adcolony.com/privacy-policy/)
- Kidoz - [Home](https://kidoz.net) - [Privacy Policy](https://kidoz.net/privacy-policy/)
- Vungle - [Home](https://vungle.com) - [Privacy Policy](https://vungle.com/privacy/)
- AppLovin - [Home](https://www.applovin.com) - [Privacy Policy](https://www.applovin.com/privacy/)
- StartApp - [Home](https://www.startapp.com) - [Privacy Policy](https://www.startapp.com/policy/privacy-policy/)
- InMobi - [Home](https://www.inmobi.com) - [Privacy Policy](https://www.inmobi.com/privacy-policy/)
- Chartboost - [Home](https://www.chartboost.com) - [Privacy Policy](https://answers.chartboost.com/en-us/articles/200780269)
- Facebook Audience Network - [Home](https://www.facebook.com/business/marketing/audience-network) - [Privacy Policy](https://developers.facebook.com/docs/audience-network/policy/)
- Yandex Ads - [Home](https://yandex.com/dev/mobile-ads/) - [Privacy Policy](https://yandex.com/legal/mobileads_sdk_agreement/)  
</details>

```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk-teen:1.9.2+'
}
```
> Some third party partners are not included and you can combine General solution with partners dependencies from Advanced integration.
***
</details>
<details><summary><b>Advanced integration (Option 2)</b></summary>
 
We support partial integration of the third party mediation sdk you really need.  
To do this, use any combination of partial dependencies. No additional code is required for each partner network. 
**Please provide us with a list of integrated dependencies so that we can make the correct settings.**

#### The first step is to add a dependency to the core of our SDK:
```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk:1.9.2+'
    ...
}
```
#### Then you can add dependencies for each required third party mediation SDK:
<details><summary>Google Ads (Admob)</summary>

[Home](https://admob.google.com/home) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://policies.google.com/technologies/ads)
```gradle
implementation 'com.google.android.gms:play-services-ads:19.6.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>Unity Ads</summary>

[Home](https://unity.com/solutions/unity-ads) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://unity3d.com/legal/privacy-policy)
```gradle
implementation 'com.unity3d.ads:unity-ads:3.6.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>Iron Source</summary>

[Home](https://www.ironsrc.com) - ~~Banner~~, Interstitial, Rewarded Video [Privacy Policy](https://developers.ironsrc.com/ironsource-mobile/air/ironsource-mobile-privacy-policy/)
```gradle
implementation 'com.ironsource.sdk:mediationsdk:7.0.4.1'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>AdColony</summary>

[Home](https://www.adcolony.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.adcolony.com/privacy-policy/)
```gradle
implementation 'com.adcolony:sdk:4.3.1'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>Vungle</summary>

[Home](https://vungle.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://vungle.com/privacy/)
```gradle
implementation 'com.vungle:publisher-sdk-android:6.8.1'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>AppLovin</summary>

[Home](https://www.applovin.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.applovin.com/privacy/)
```gradle
implementation 'com.applovin:applovin-sdk:9.14.12+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>InMobi</summary>

[Home](https://www.inmobi.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.inmobi.com/privacy-policy/)
```gradle
implementation 'com.inmobi.monetization:inmobi-ads:9.1.1'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>StartApp</summary>

[Home](https://www.startapp.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.startapp.com/policy/privacy-policy/)
```gradle
implementation 'com.startapp:inapp-sdk:4.7.5+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>Kidoz</summary>

[Home](https://kidoz.net) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://kidoz.net/privacy-policy/)
```gradle
implementation 'com.kidoz.sdk:KidozSDK:8.9.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>Chartboost</summary>

[Home](https://www.chartboost.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://answers.chartboost.com/en-us/articles/200780269)
```gradle
implementation 'com.chartboost:chartboost-sdk:8.2.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details><details><summary>SuperAwesome</summary>

[Home](https://www.superawesome.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.superawesome.com/privacy-hub/privacy-policy/)   
**Works to children audience only**.
```gradle
implementation 'tv.superawesome.sdk.publisher:superawesome:7.2.16+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
```
</details>

#### To the following third party mediation SDK, be sure to add our additional support dependency `mediation-teen` for non-certified ad SDK in the [Families Ads program](https://support.google.com/googleplay/android-developer/answer/9283445).
```gradle
implementation 'com.cleversolutions.ads:mediation-teen:1.9.0+'
```
<details><summary>Facebook Audience Network</summary>

[Home](https://www.facebook.com/business/marketing/audience-network) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://developers.facebook.com/docs/audience-network/policy/)
```gradle
implementation 'com.facebook.android:audience-network-sdk:6.2.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details><details><summary>Yandex Ads</summary>

[Home](https://yandex.com/dev/mobile-ads/) - Banner, Interstitial, ~~Rewarded Video~~ - [Privacy Policy](https://yandex.com/legal/mobileads_sdk_agreement/)
```gradle
implementation 'com.yandex.android:mobileads:3.1.1'
implementation 'com.yandex.android:mobmetricalib:3.14.3'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details>

Third party mediation SDK of Beta third party partners:
> :warning: Next third party mediation SDK in closed beta and available upon invite only. If you would like to be considered for the beta, please contact Support.  

<details><summary>MoPub</summary>

[Home](https://www.mopub.com/) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.mopub.com/en/legal/privacy)
```gradle
implementation 'com.mopub:mopub-sdk-banner:5.15.0+'
implementation 'com.mopub:mopub-sdk-fullscreen:5.15.0+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details><details><summary>Verizon Media</summary>

[Home](https://www.verizonmedia.com/advertising/solutions#/mobile) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.verizonmedia.com/policies/us/en/verizonmedia/privacy/)
```gradle
implementation 'com.verizon.ads:android-vas-standard-edition:1.8.2+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details><details><summary>MyTarget</summary>

[Home](https://target.my.com/) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://legal.my.com/us/mytarget/privacy/)  
**Works to CIS countries only**.
```gradle
implementation 'com.my.target:mytarget-sdk:5.11.9+'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details><details><summary>MobFox</summary>

[Home](https://www.mobfox.com) - Banner, Interstitial, Rewarded Video - [Privacy Policy](https://www.mobfox.com/privacy-policy/)
```gradle
implementation 'com.github.mobfox:mfx-android-sdk:4.3.2+'
implementation 'com.android.volley:volley:1.1.1'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details><details><summary>Amazon Ads</summary>

[Home](https://advertising.amazon.com) - Banner, ~~Interstitial, Rewarded Video~~ - [Privacy Policy](https://advertising.amazon.com/legal/privacy-notice)
```gradle
implementation 'com.amazon.android:mobile-ads:6.0.0'

implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
implementation "com.cleversolutions.ads:mediation-teen:$casVersion"
```
</details>

> The list of partners networks may change in the future.
***
</details>

## Step 2 Add Cross Promotion SDK
Cross promotion is an app marketing strategy in which app developers promote one of their titles on another one of their titles. Cross promoting is especially effective for developers with large portfolios of games as a means to move users across titles and use the opportunity to scale each of their apps. This is most commonly used by hyper-casual publishers who have relatively low retention, and use cross promotion to keep users within their app portfolio.

Start your cross promotion campaign with CAS [here](https://cleveradssolutions.com).

<details><summary><b>Integration</b></summary>

```gradle
dependencies {
  ...
  implementation "com.cleversolutions.ads:cas-sdk:$casVersion"
  implementation "com.cleversolutions.ads:cas-promo:$casVersion"
}
```
</details>
<details><summary><b>Do not advertise installed apps to the user</b></summary>

[Android 11 changes how apps can query and interact with other apps that the user has installed on a device.](https://developer.android.com/about/versions/11/privacy/package-visibility)  
Using the `<queries>` element, apps can define the set of other packages that they can access. This element helps encourage the principle of least privilege by telling the system which other packages to make visible to your app, and it helps app stores like Google Play assess the privacy and security that your app provides for users.

If your app targets Android 11 or higher, you might need to add the `<queries>` element in your app's manifest file. Within the `<queries>` element, you can specify packages by names of others applications involved in Cross promotion.
```xml
<manifest package="com.example.game">
  <queries>
      <!-- Specific apps you interact with, eg: -->
      <package android:name="com.example.app1" />
      <package android:name="com.example.app2" />
  </queries>
  ...
</manifest>
```
***
</details>

## Step 3 Gradle settings
<details><summary><b>AndroidX</b></summary>
 
As of SDK 18.0.0, AdMob migrated from Android Support Libraries to Jetpack (AndroidX) Libraries. Refer to the [Google Play services release notes](https://developers.google.com/android/guides/releases#june_17_2019) for more information.  

Due to this, we working with the AdMob adapter it’s required that your project migrates from Android Support Libraries to Jetpack Libraries (Android X) if you are using any. Please refer to [Migrating to AndroidX](https://developer.android.com/jetpack/androidx/migrate) for more information.  

In case you can not migrate the project using this tool, you can use the following flags in gradle.properties, to build your project using AndroidX. 
-  android.useAndroidX = true  
-  android.enableJetifier = true  
***
</details><details><summary><b>MultiDEX</b></summary>
 
At times, including the CAS SDK may cause the 64K limit on methods that can be packaged in an Android dex file to be breached. This can happen if, for example, your app packs a lot of features for your users and includes substantive code to realize this.  

If this happens, you can use the multidex support library to enable building your app correctly. To do this:  
- Modify the **defaultConfig** to mark your application as multidex enabled:  
```gradle
defaultConfig {
   ...
   multiDexEnabled true // add this to enable multi-dex
}
```
- Add the following line to the dependencies element in your application build script. 
```gradle
dependencies {
    implementation 'androidx.multidex:multidex:2.0.1'
    ...
}
```

> You can read more about MultiDex on the [Android Deleveloper page](https://developer.android.com/studio/build/multidex).
***
</details><details><summary><b>Java Version</b></summary>
 
Our SDK requires for correct operation to determine the Java version in Gradle. Add the following line to the android element in your application module’s build script. 
```gradle
android{
    ...
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
```
***
</details>

## Step 4 Update AndroidManifest
<details><summary><b>Manifest Permissions</b></summary>
 
Add the following permissions to your AndroidManifest.xml file inside the manifest tag but outside the <application> tag:
```xml
<manifest>
 <uses-permission android:name="android.permission.INTERNET" />
 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
</manifest>
```
#### Optional Permissions
This permission is used for certain ads that vibrate during play. This is a normal level permission, so this permission just needs to be defined in the manifest to enable this ad feature.
```xml
<manifest>
 <uses-permission android:name="android.permission.VIBRATE" />
</manifest>
```
This permission is used for certain ads that allow the user to save a screenshot to their phone. 
Note that with this permission on devices running Android 6.0 (API 23) or higher, this permission must be requested from the user. 
See [Requesting Permissions](https://developer.android.com/training/permissions/requesting) for more details. 
```xml
<manifest>
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
</manifest>
```
This permission is not a mandatory permission, however, including it will enable accurate ad targeting.
```xml
<manifest>
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
</manifest>
```
***
</details><details><summary><b>Admob App ID</b></summary>

Follow the [link](http://psvpromo.psvgamestudio.com/cas-settings.php) to get your Admob App ID.  

Add your AdMob App ID to your app's AndroidManifest.xml file by adding a <meta-data> tag with name com.google.android.gms.ads.APPLICATION_ID, as shown below.  
  
For android:value insert your own AdMob App ID in quotes, as shown below.
```xml
<manifest>
  <application>
    ...
    <!-- Sample AdMob App ID: ca-app-pub-3940256099942544~3347511713 -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    <!-- Delay app measurement until MobileAds.initialize() is called. -->
    <meta-data
        android:name="com.google.android.gms.ads.DELAY_APP_MEASUREMENT_INIT"
        android:value="true"/>
  </application>
</manifest>
```
***
</details>

## Step 5 Add the CAS default settings file
**Optional step.**
Follow the [link](http://psvpromo.psvgamestudio.com/cas-settings.php) to download a cas_settings.json file.

Drop the **cas_settings.json** into the `src/res/raw/` folder to your project.  

## Step 6 Import the CAS SDK

```java
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
```

<details><summary><b>Main CAS class</b></summary>

The `CAS` class gives access to all the possibilities of the SDK.  
Singleton instance of `AdsSettings` to configure all mediation managers.
```java
AdsSettings sdkSettings = CAS.getSettings();
```
Singleton instance of `TargetingOptions` to inform SDK of the users details.
```java
TargetingOptions targetingOptions = CAS.getTargetingOptions();
```
Last initialized instance of `MediationManager` stored with strong pointer. May be **null** before calling the `CAS.initialize` function.
> :warning: It is not recommended that you rely on this static method to always return a manager reference.  
> Your application process can be killed and all static fields cleared.  
> Therefore, we recommend storing a non-static reference to the initialized `MediationManager` in your `Application` or `Activity` class and re-initialize if the reference is lost.
```java
MediationManager lastManager = CAS.getManager();
```
The CAS SDK version string.
```java
String sdkVersion = CAS.getSDKVersion();
```
***
</details><details><summary><b>Verify Your Integration (Optional)</b></summary>

The CAS SDK provides an easy way to verify that you’ve successfully integrated any additional adapters; it also makes sure all required dependencies and frameworks were added for the various mediated ad networks.   
After you have finished your integration, call the following static method and confirm that all networks you have implemented are marked as VERIFIED:  
```java
CAS.validateIntegration(activity, appContentRating);
```
Find log information by tag: **CASIntegrationHelper**

Once you’ve successfully verified your integration, please remember to **remove the integration helper from your code**.

The Integration Helper tool reviews everything, including ad networks you may have intentionally chosen NOT to include in your application. These will appear as MISSING and there is no reason for concern. In the case the ad network’s integration has not been completed successfully, it will be marked as NOT VERIFIED.  
***
</details>
<details><summary><b>Don’t forget to implement app-ads.txt (Optional)</b></summary>

For both iOS and Android integrations, we encourage our partners to adopt this file and help us combat ad fraud.  
Read detailed instructions on [how to create and upload your app-ads.txt file](https://github.com/cleveradssolutions/App-ads.txt#cleveradssolutions-app-adstxt).
***
</details>

## Step 7 Privacy Laws
This documentation is provided for compliance with various privacy laws. If you are collecting consent from your users, you can make use of APIs discussed below to inform CAS and all downstream consumers of this information.  

A detailed article on the use of user data can be found in the [Privacy Policy](/../../wiki/Privacy-Policy).

### GDPR Managing Consent
<details>
 
This documentation is provided for compliance with the European Union's [General Data Protection Regulation (GDPR)](https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=CELEX:32016R0679). In order to pass GDPR consent from your users, you should make use of the APIs and methods discussed below to inform CAS and all downstream consumers of this information.  

**Passing Consent** to CAS API, use this functions:  
If the user provided consent, please set the following flag to "true":
```java
CAS.getSettings().setConsent(true);
```
If the user did not consent, please set the following flag to "false":
```java
CAS.getSettings().setConsent(false);
```
</details>

### CCPA Compliance
<details>
 
This documentation is provided for compliance with the California Consumer Privacy Act (CCPA). In order to pass CCPA opt-outs from your users, you should make use of the APIs discussed below to inform CAS and all downstream consumers of this information.  

**Passing Opt-outs**
In the Android SDK v1.4, we added privacy methods to our AdsSettings API for additional support in CCPA compliance. To successfully pass us an opt-out signal, the publisher will need to provide CAS a signal to indicate whether CCPA legislation is applicable to the user in addition to the actual consent value.  

A value of "false" implies the user has **not** opted-out to the sale of their data, as defined by the CCPA, and CAS should continue with our standard processing of user information.  
```java
CAS.getSettings().setDoNotSell(false);
```
A value of "true" means the user has opted-out to the sale of their data.  
```java
CAS.getSettings().setDoNotSell(true);
```
</details>

###  COPPA and EEA Compliance
<details>
 
This documentation is provided for additional compliance with the [Children’s Online Privacy Protection Act (COPPA)](https://www.ftc.gov/tips-advice/business-center/privacy-and-security/children%27s-privacy). Publishers may designate all inventory within their applications as being child-directed or as COPPA-applicable though our UI. Publishers who have knowledge of specific individuals as being COPPA-applicable should make use of the API discussed below to inform CAS and all downstream consumers of this information.  

You can mark your ad requests to receive treatment for users in the European Economic Area (EEA) under the age of consent. This feature is designed to help facilitate compliance with the General Data Protection Regulation (GDPR). Note that you may have other legal obligations under GDPR. Please review the European Union’s guidance and consult with your own legal counsel. Please remember that CAS tools are designed to facilitate compliance and do not relieve any particular publisher of its obligations under the law.

Call "true" indicate that you want your content treated as child-directed for purposes of COPPA or receive treatment for users in the European Economic Area (EEA) under the age of consent. 
```java
CAS.getSettings().setTaggedForChildren(true);
```
Call "false" to indicate that you **don't** want your content treated as child-directed for purposes of COPPA or **not** receive treatment for users in the European Economic Area (EEA) under the age of consent.
```java
CAS.getSettings().setTaggedForChildren(false);
```
</details>

**We recommend to set Privacy API before initializing CAS SDK.**

## Step 8 Configuring CAS SDK
<details><summary><b>Debug mode</b></summary>

The enabled Debug Mode will display a lot of useful information for debugging about the states of the sdk with tag CAS.   
Disabling Debug Mode may improve application performance.  
Disabled by default.  
```java
CAS.getSettings().setDebugMode(true);
```
***
</details>
<details><summary><b>Test Device IDs</b></summary>

Identifiers corresponding to test devices which will always request test ads.  
The test device identifier for the current device is logged to the console when the first ad request is made.  

```java
CAS.getSettings().setTestDeviceIDs(new HashSet<String>(...));
CAS.getSettings().getTestDeviceIDs().add("test-device-id");
```
***
</details>
<details><summary><b>Muted Ad sounds</b></summary>

Indicates if the application’s audio is muted. Affects initial mute state for all ads.  
Use this method only if your application has its own volume controls.  
```java
CAS.getSettings().setMutedAdSounds(true);
```
***
</details>
<details><summary><b>Waterfall Loading Mode</b></summary>

|        Mode        |  Load<sup>[*1](#load-f-1)</sup>  | Impact on App performance | Memory usage |        Actual ads<sup>[*2](#actual-f-2)</sup>       |
|:------------------:|:------:|:-------------------------:|:------------:|:------------------------:|
|   FastestRequests  |  Auto  |         Very high         |     High     |       Most relevant      |
|    FastRequests    |  Auto  |            High           |    Balance   |      High relevance      |
|  Optimal *(Default)*  |  Auto  |          Balance          |    Balance   |          Balance         |
|   HighPerformance  |  Auto  |            Low            |      Low     |       Possible loss      |
| HighestPerformance |  Auto  |          Very low         |      Low     |       Possible loss      |
|       Manual      | Manual<sup>[*3](#manual-f-3)</sup> |          Very low         |      Low     | Depends on the frequency |

```java
CAS.getSettings().setLoadingMode(LoadingManagerMode.Optimal);
```

<b id="load-f-1">^1</b>: Auto control load mediation ads starts immediately after initialization and will prepare displays automatically.  

<b id="actual-f-2">^2</b>: Potential increase in revenue by increasing the frequency of ad requests. At the same time, it greatly affects the performance of the application.  

<b id="manual-f-3">^3</b>: Manual control loading mediation ads requires manual preparation of advertising content for display. Use ad loading methods before trying to show: `MediationManager.loadInterstitial(), MediationManager.loadRewardedVideo(), CASBannerView.loadNextAd()`.  
***
</details>
<details><summary><b>Analytics collection</b></summary>

Sets CAS analytics collection is enabled for this app on this device.    
By default it is disabled. This setting is persisted across app sessions.  
```java
CAS.getSettings().setAnalyticsCollectionEnabled(true);
```

if your application uses [Firebase Analytics](https://firebase.google.com/docs/analytics)  then this service will be used by default.
You also have the option to implement a custom analytics event handler:
```java
import android.os.Bundle;
import com.cleversolutions.basement.CASAnalytics;
class CustomCASAnalyticsHandler:CASAnalytics.Handler{
  public override void log(String eventName, Bungle content){
    // TODO: Handle event content
  }
  
  public static void attachHandler(){
     CASAnalytics.setHandler(new CustomCASAnalyticsHandler());
     CAS.getSettings().setAnalyticsCollectionEnabled(true);
  }
}
```
***
</details>
<details><summary><b>Targeting Options</b></summary>
 
You can now easily tailor the way you serve your ads to fit a specific audience!  
You’ll need to inform our servers of the users’ details so the SDK will know to serve ads according to the segment the user belongs to.

```java
// Set user age. Limitation: 1-99 and 0 is 'unknown'
CAS.getTargetingOptions().setAge(12)
// Set user gender
CAS.getTargetingOptions().setGender(TargetingOptions.GENDER_MALE)
// The user's current location.
// Location data is not used to CAS; however, it may be used by third party ad networks.
// Do not use Location just for advertising.
// Your app should have a valid use case for it as well.
CAS.getTargetingOptions().setLocation(userLocation)
```
***
</details>

## Step 9 Initialize the SDK
<details><summary><b>Initialize MediationManager instance</b></summary>
 
 An `managerID` is a unique ID number assigned to each of your ad placements when they're created in CAS. The manager ID is added to your app's code and used to identify ad requests. If you haven't created an CAS account and registered an app yet, now's a great time to do so at [cleveradssolutions.com](https://cleveradssolutions.com). In a real app, it is important that you use your actual CAS manager ID.  
```java
class MainActivity extends Activity implements OnInitializationListener{
  MediationManager manager;
  void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Configure AdsSettings before initialize
      manager = CAS.buildManager(this)
                   .withManagerId(own_identifier)
                   .withInitListener(this)
                   .withEnabledAdTypes(AdTypeFlags.Everything)
                   .withTestAdMode(isTestBuild)
                   .initialize();
      );  
  }

  public override void onInitialization(boolean success, @Nullable String error){
    // CAS manager initialization done 
  }
}
```
***
</details>
<details><summary><b>Subscribe listener to Ad Loading response</b></summary>

```java
manager.getOnAdLoadEvent().add(new AdLoadCallback(){
    @AnyThread
    override void onAdFailedToLoad(AdType type, String error){
        // Callback on AdType failed to load and cant be shown.
    }
    
    @AnyThread
    override void onAdLoaded(AdType type){
        // Callback on AdType loaded and ready to shown.
    }
})
```
***
</details>
<details><summary><b>Last page Ad</b></summary>

The latest free ad page for your own promotion.  
This ad page will be displayed when there is no paid ad to show or internet availability.  
**Attention!** Impressions and clicks of this ad page will not be billed.  
By default, this page will not be displayed while the ad content is NULL.  
The Last Page Ad Content will be shown as a Banner and Interstitial ads.
```java
manager.setLastPageAdContent(new LastPageAdContent(
    headline, // The message that you want users to see.
    adText,   // A description for the app being promoted.
    destinationURL, // The URL that CAS will direct users to when they click the ad.
    imageURL, // The direct URL of the image to be used as the ad file.
    iconURL   // The direct URL of the icon or logo (Small square picture).
));
```
> `LastPageAdContent.destinationURL` is not visible in the ad and should always have a non-empty URL.
***
</details>

## Step 10 Implement our Ad Units
### Banner Ad
Banner ads are displayed in CASBannerView objects from module CleverAdsSolutions, so the first step toward integrating banner ads is to include a CASBannerView in your view hierarchy. This is typically done either with the layout or programmatically.
<details><summary><b>Add to the layout</b></summary>
 
The first step toward displaying a banner is to place CASBannerView in the layout for the Activity or Fragment in which you'd like to display it. The easiest way to do this is to add one to the corresponding XML layout file. Here's an example that shows an activity's CASBannerView:  
```xml
# main_activity.xml
...
  <com.cleversolutions.ads.android.CASBannerView 
      xmlns:ads="http://schemas.android.com/apk/res-auto"
      android:id="@+id/bannerView"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:gravity="center"
      ads:bannerSize="Standard320x50"/>
...
```
Note the following required attributes:  
ads:bannerSize - Set this to the ad size you'd like to use. If you don't want to use the standard size defined by the constant, you can set a custom size instead. See the banner size section below for details.  
***
</details>
<details><summary><b>Create programmatically</b></summary>
 
```java
CASBannerView bannerView = new CASBannerView(this, manager);

// Select banner Size
bannerView.setSize(AdSize.Standard320x50);
// OR same
// manager.bannerSize = AdSize.Standard320x50

bannerView.setPosition(AdPosition.Center);
bannerView.setListener(new AdCallback(){...});

// Add bannerView to your view hierarchy.
parentView.addView(bannerView);
// OR
activity.addContentView(bannerView, new LayoutParams(...));
```
***
</details>
<details><summary><b>Load Banner Ad</b></summary>
 
Manual load manager mode require call `loadNextAd()` after create `CASBannerView` and change banner size.  
You can use `loadNextAd()` for cancel current impression and load next ad.
```java
bannerView.loadNextAd();
```
***
</details>
<details><summary><b>Ad Size</b></summary>
 
| Size in dp (WxH) |      Description     |    Availability    |  AdSize constant |
|:----------------:|:--------------------:|:------------------:|:----------------:|
|      320x50      |    Standard Banner   | Phones and Tablets |      BANNER      |
|      728x90      |    IAB Leaderboard   |       Tablets      |    LEADERBOARD   |
|      300x250     | IAB Medium Rectangle | Phones and Tablets | MEDIUM_RECTANGLE |

#### Adaptive Banners
Adaptive banners are the next generation of responsive ads, maximizing performance by optimizing ad size for each device.  
To pick the best ad size, adaptive banners use fixed aspect ratios instead of fixed heights. This results in banner ads that occupy a more consistent portion of the screen across devices and provide opportunities for improved performance. [You can read more in this article.](https://developers.google.com/admob/android/banner/adaptive)

Use the appropriate static methods on the ad size class, such as AdSize.getAdaptiveBanner(context, maxWidthDPI) to get an adaptive AdSize object.
```java
// Get adaptive size in container view group:
adaptiveSize = AdSize.getAdaptiveBanner(viewGroup);
// Get adaptive size in full screen width:
adaptiveSize = AdSize.getAdaptiveBannerInScreen(context);
// Get adaptive size with width parameter:
adaptiveSize = AdSize.getAdaptiveBanner(context, maxWidthDPI);

// After create Apadtive size need call MediationManager:
manager.setBannerSize(adaptiveSize);
// OR same to CASBannerView
bannerView.setSize(adaptiveSize);
```

#### Smart Banners
Typically, Smart Banners on phones have a BANNER size. Or on tablets a LEADERBOARD size.

Use the static method getSmartBanner(context) in the AdSize class to get the smart AdSize object.
```java
smartSize = AdSize.getSmartBanner(context);

// After create Smart size need call MediationManager:
manager.setBannerSize(smartSize);
// OR same to CASBannerView
bannerView.setSize(smartSize);
```
***
</details>
<details><summary><b>Banner refresh rate</b></summary>
 
An ad unit’s automatic refresh rate determines how often a new ad request is generated for that ad unit.  
We recommend using 30 seconds (Default) refresh rate.  
You may also set a custom refresh rate of 5-360 seconds.  
This setting is available for change through the web application settings.   

You can specify refresh rate **before** initialization to allow overrides settings by the web interface for a given session.
```java
CAS.getSettings().setBannerRefreshInterval(interval);
manager = CAS.initialize(...);
```
Or **after** initialization to override the web application settings for a given session.
```java
manager = CAS.initialize(..., new OnInitializationListener(){  
    void onInitialization(boolean success, String error){  
        // CAS manager initialization done  
        CAS.getSettings().setBannerRefreshInterval(interval);
    }  
} );
```
***
</details>

### AdCallback
Through the use of AdCallback, you can listen for lifecycle events, such as when an ad is closed or the user leaves the app.  

<details>
 
To register for ad events, set the `listener` property on CASBannerView to an object for banner or set argument on show ad, that implements the AdCallback protocol. Generally, the class that implements banner ads also acts as the listener class, in which case, the `listener` property can be set to `this`. 
```java
// Executed when the ad is displayed.
// @param ad Information of displayed ad.
void onShown(com.cleversolutions.ads.AdStatusHandler ad);

// Executed when the ad is failed to display.
// The Banner may automatically appear when the Ad is ready again.
// This will trigger the [onShown] callback again.
void onShowFailed(String message);

// Executed when the user clicks on an ad.
void onClicked();

// Executed when the Ad is completed.
// Banner Ad does not use this callback.
void onComplete();

// Executed when the ad is closed.
// The Banner Ad cannot be displayed automatically after this callback for the current view.
// If you decide to show the Banner Ad on this view then you need refresh view visibility.
void onClosed();
```
***
</details>
 
### Check Ad Availability
You can ask for the ad availability directly by calling the following function:
```java
manager.isAdReady(AdType.Interstitial); //Check ready any AdType
```

### Show Interstitial Ad
**Manual load manager mode** require call `loadInterstitial` before try show ad.  
You will also need to load new ad after the ad closed.  
```java
manager.loadInterstitial();
```

Invoke the following method to serve an selected ad to your users:
```java
manager.show(AdType.Interstitial, 
        // Optional. AdCallback implementation
        new AdCallback(){...}
      );
```

<details><summary><b>Minimum interval between Interstitial ads</b></summary>
 
You can limit the posting of an interstitial ad to a period of time in seconds after the ad is closed, during which display attempts will fail.  
This setting is available for change through the web application settings.  Unlimited by default (0 seconds).

You can specify minimal interval **before** initialization to allow overrides settings by the web interface for a given session.
```java
CAS.getSettings().setInterstitialInterval(interval);
manager = CAS.initialize(...);
```
Or **after** initialization to override the web application settings for a given session.
```java
manager = CAS.initialize(..., new OnInitializationListener(){  
    void onInitialization(boolean success, String error){  
        // CAS manager initialization done  
        CAS.getSettings().setInterstitialInterval(interval);
    }  
} );
```

You can also restart the countdown interval until the next successful ad shown. For example, after closing Rewarded Video Ad.
```java
CAS.getSettings().restartInterstitialInterval();
``` 
***
</details>

### Show Rewarded Video Ad
 **Manual load manager mode** require call `loadRewardedVideo` before try show ad.  
You will also need to load new ad after the ad closed.  
```java
manager.loadRewardedVideo();
```

Invoke the following method to serve an selected ad to your users:
```java
manager.show(AdType.Rewarded, 
        // Optional. AdCallback implementation
        new AdCallback(){...}
      );
```

<details><summary><b>Redirect rewarded video ad impressions to interstitial ads at higher cost per impression</b></summary>
 
This option will compare ad cost and serve regular interstitial ads when rewarded video ads are expected to generate less revenue.  
Interstitial Ads does not require to watch the video to the end, but the `AdCallback.onComplete` callback will be triggered in any case.  
```java
CAS.getSettings().setAllowInterstitialAdsWhenVideoCostAreLower(true);
```
Disabled by default.
***
</details>

## Mediation extras
The CAS mediation adapters provides the `AdNetwork` constant keys to customize parameters to be sent to networks SDK.  

The following sample code demonstrates how to pass these parameters to the networks adapter:
```java
manager = CAS.buildManager(this)
 .withMediationExtras(AdNetwork.ADCOLONY_MULTIWINDOW, "0")
 .withMediationExtras(AdNetwork.VUNGLE_ANDROID_ID_OPTED_OUT, "0")
 .initialize();
```


Although the GDPR and CCPA settings are used by all media adapters, you have the option to override these values for a specific network.
<details><summary>Google Ads</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.ADMOB_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.ADMOB_CCPA_OPTED_OUT, "1")
 .initialize();
```
See Admob [GDPR](https://developers.google.com/admob/android/eu-consent) and [CCPA](https://developers.google.com/admob/android/ccpa#rdp_signal) implementation details for more information about what values may be provided in these methods.
***
</details><details><summary>AppLovin</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.APPLOVIN_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.APPLOVIN_CCPA_OPTED_OUT, "1")
 // Initialize MAX
 .withMediationExtras(AdNetwork.APPLOVIN_USE_MAX, "1")
 .initialize();
```
***
</details><details><summary>AdColony</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.ADCOLONY_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.ADCOLONY_CCPA_OPTED_OUT, "1")
 // Used to alert AdColony that multi-window is enabled for your app, 
 // allowing us to adjust our interstitial layout as necessary.
 .withMediationExtras(AdNetwork.ADCOLONY_MULTIWINDOW, "1")
 // Optionally set the origin store for this app (default: 'google').
 .withMediationExtras(AdNetwork.ADCOLONY_ORIGINSTORE, origin_store)
 .initialize();
```
See [AdColony’s Privacy Laws implementation details](https://github.com/AdColony/AdColony-Android-SDK/wiki/Privacy-Laws) for more information about what values may be provided in these methods.
***
</details><details><summary>Vungle</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.VUNGLE_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.VUNGLE_CCPA_OPTED_OUT, "1")
 // Set Minimum Disk Space
 .withMediationExtras(AdNetwork.VUNGLE_MINIMUM_SPACE_FOR_INIT, String.valueOf(51L * 1024L * 1024L))
 .withMediationExtras(AdNetwork.VUNGLE_MINIMUM_SPACE_FOR_AD, String.valueOf(50L * 1024L * 1024L))
 // Restrict Use of Device ID
 .withMediationExtras(AdNetwork.VUNGLE_ANDROID_ID_OPTED_OUT, "1")
 .initialize();
```
See [Vungle's Advanced Settings implementation](https://support.vungle.com/hc/en-us/articles/360047780372) for more information.
***
</details><details><summary>IronSource</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.IRONSOURCE_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.IRONSOURCE_CCPA_OPTED_OUT, "1")
 .initialize();
```
See [ironSource's managing consent](https://developers.ironsrc.com/ironsource-mobile/android/advanced-settings/) documentation for more details.
***
</details><details><summary>Unity Ads</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.UNITYADS_GDPR_CONSENT, "0")
 // User CCPA do not sell data "1" and "0" is use data in ad
 .withMediationExtras(AdNetwork.UNITYADS_CCPA_OPTED_OUT, "1")
 .initialize();
```
See [Unity Ads data privacy and consent implementation details](https://unityads.unity3d.com/help/legal/data-privacy-and-consent) for more information about what values may be provided in these methods.
***
</details><details><summary>InMobi</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.APPLOVIN_GDPR_CONSENT, "0")
 .withMediationExtras(AdNetwork.INMOBI_GDPR_IAB, iab_string)
 .initialize();
```
See [InMobi's GDPR implementation details](https://support.inmobi.com/monetize/android-guidelines) for more information about the possible keys and values that InMobi accepts in this consent object.
***
</details><details><summary>StartApp</summary>

```java
manager = CAS.buildManager(this)
 // User GDPR consent "1" is accepted and "0" is rejected
 .withMediationExtras(AdNetwork.STARTAPP_GDPR_CONSENT, "0")
 .initialize();
```
***
</details>

>Unique properties for each network will be added in the future.  

## GitHub issue tracker
To file bugs, make feature requests, or suggest improvements for the Android SDK, please use [GitHub's issue tracker](https://github.com/cleveradssolutions/CAS-Android/issues).

## Support
Technical support: Max  
Skype: m.shevchenko_15  

Network support: Vitaly  
Skype: zanzavital  

mailto:support@cleveradssolutions.com  

## License
The CAS Android-SDK is available under a commercial license. See the LICENSE file for more info.
