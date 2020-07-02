# CleverAdsSolutions-Android SDK Integration 
[ ![Downlaod](https://api.bintray.com/packages/cleveradssolutions/CAS-Android/cas-sdk/images/download.svg) ](https://bintray.com/cleveradssolutions/CAS-Android)

## Before You Start
We support Android Operating Systems Version 4.2 (API level 17) and up.  

### AndroidX
As of SDK 18.0.0, AdMob migrated from Android Support Libraries to Jetpack (AndroidX) Libraries. Refer to the [Google Play services release notes](https://developers.google.com/android/guides/releases#june_17_2019) for more information.  

Due to this, we working with the AdMob adapter it’s required that your project migrates from Android Support Libraries to Jetpack Libraries (Android X) if you are using any. Please refer to [Migrating to AndroidX](https://developer.android.com/jetpack/androidx/migrate) for more information.  

In case you can not migrate the project using this tool, you can use the following flags in gradle.properties, to build your project using AndroidX. 
*  android.useAndroidX = true  
*  android.enableJetifier = true  

### Ad audiences
Some Ad Networks target specific age ratings for your app’s content. Please select your content rating in application and follow the instructions.  
*  G - 0+ years. General audiences. Content suitable for all audiences, including families and children.  
*  PG - 7+ years. Parental guidance. Content suitable for most audiences with parental guidance, including topics like non-realistic, cartoonish violence.  
*  T - 12+ years. Teen. Content suitable for teen and older audiences, including topics such as general health, social networks, scary imagery, and fight sports.  
*  MA - 18+ years. Mature audiences. Content suitable only for mature audiences; includes topics such as alcohol, gambling, sexual content, and weapons.  
**You can be punished if you don’t comply with the partner’s content rating restrictions!**

# Table of contents
 1.  [Add the CAS SDK to Your Project](#step-1-add-the-cas-sdk-to-your-project)  
 2.  [Gradle settings](#step-2-gradle-settings)
 3.  [Add mediation SDK](#step-3-add-mediation-sdk)  
 4.  [Add Cross Promotion SDK](#step-4-add-cross-promotion-sdk)  
 5.  [Update AndroidManifest](#step-5-update-androidmanifest)  
 6.  [GDPR Managing Consent](#step-6-gdpr-managing-consent)
 7.  [Verify Your Integration](#step-7-verify-your-integration)
 8.  [Initialize the SDK](#step-8-initialize-the-sdk)  
 9.  [Implement our Ad Units](#step-9-implement-our-ad-units)  
 9.1. [Native Ads](#native-ads)  
 9.2. [Banner Ad](#banner-ad)  
 9.3. [AdCallback](#adcallback)  
 9.4. [Check Ad Availability](#check-ad-availability)  
 9.5. [Show fullscreen Ad](#show-fullscreen-ad)  
 10.  [Adding App-ads.txt file of our partners](#step-10-adding-app-ads-txt-file-of-our-partners)  
 11.  [Mediation partners](#mediation-partners)  
 12.  [Support](#support). 

## Step 1 Add the CAS SDK to Your Project

### Option 1 Gradle Integration
Add one of the following sdk to the dependencies section for your ad audience.
- G - 0+ years General audiences SDK and skip [Step 3](#step-3-add-mediation-sdk)  
```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk-general:1.3.5' 
}
```
- T - 12+ years. Teen audiences SDK and skip [Step 3](#step-3-add-mediation-sdk)  
```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk-teen:1.3.5'
}
```
- SDK without mediation dependencies. Follow [Step 3](#step-3-add-mediation-sdk) to integrate mediation SDK.
```gradle
dependencies {
    implementation 'com.cleversolutions.ads:cas-sdk:1.3.5' 
}
```

### Option 2 Manual Integration
1.  Download the latest release binaries from GitHub, specifically [CleverAdsSolutions.aar](https://github.com/cleveradssolutions/CAS-Android/releases/latest)
2.  Create or open your existing Android project in Android Studio.
3.  Add new module and import CleverAdsSolutions.aar. Name the module "CleverAdsSolutions" for example.
4.  Open Module Settings for the app and add "CleverAdsSolutions" module as a dependency.  
5.  Add following dependencies of support libraries:  
```gradle
dependencies {
      ...
      implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
      implementation 'com.google.code.gson:gson:2.8.5'
      implementation 'androidx.appcompat:appcompat:1.1.0'
      implementation 'androidx.multidex:multidex:2.0.1'
      implementation 'androidx.core:core:1.1.0'
      implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
      implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
      implementation 'androidx.legacy:legacy-support-v4:1.0.0'
      implementation 'androidx.recyclerview:recyclerview:1.0.0'
      implementation 'com.squareup.picasso:picasso:2.71828'
      implementation 'androidx.browser:browser:1.2.0'
      implementation 'com.google.android.gms:play-services-ads-identifier:17.0.0'
      implementation 'com.google.android.gms:play-services-basement:17.3.0'
      implementation 'com.google.android.gms:play-services-base:17.3.0'
      implementation 'org.greenrobot:eventbus:3.1.1'
}
```

## Step 2 Gradle settings
Add the following to your app’s **build.gradle** file inside repositories section:
```gradle
repositories {
      google()
      jcenter()
      maven { url "https://dl.bintray.com/cleveradssolutions/CAS-Android" }
      maven { url "https://adcolony.bintray.com/AdColony" }
      maven { url "https://dl.bintray.com/ironsource-mobile/android-sdk" }
      maven { url "https://maven.google.com" }
      maven { url "https://chartboostmobile.bintray.com/Chartboost" }
      maven { url 'http://dl.bintray.com/gabrielcoman/maven' }
      maven { url "http://dl.bintray.com/superawesome/SuperAwesomeSDK" }
      ...
}
```

#### MultiDEX
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
    // AndroidX dependency
    implementation 'androidx.multidex:multidex:2.0.1'

    // OR Legacy Support dependency
    implementation 'com.android.support:multidex:1.0.3'
}
```

#### Java Version  
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

## Step 3 Add mediation SDK  
The CAS Mediation platform supports interstitial and video ads from 12 leading ad networks, equipped with smart loading, ad placement technology and ad delivery optimization. Our Mediation solution is a new monetization tool that enhances user experience, offers better control on ad performance and significantly increases revenue!

Skip Step 3 if use gradle integrate **cas-sdk-general** or **cas-sdk-teen**

Add following dependencies of Mediation Ad Network SDK:  
```gradle
dependencies {
      ...
      implementation 'com.google.android.gms:play-services-ads:19.2.0'
      implementation 'com.kidoz.sdk:KidozSDK:0.8.8.8'
      implementation 'com.vungle:publisher-sdk-android:6.7.0'
      implementation 'com.adcolony:sdk:4.1.4'
      implementation 'com.startapp:inapp-sdk:4.6.+'
      implementation 'com.ironsource.sdk:mediationsdk:6.17.0'
      implementation 'com.applovin:applovin-sdk:9.12.+'
      implementation 'com.inmobi.monetization:inmobi-ads:9.0.4'
      implementation 'com.chartboost:chartboost-sdk:8.0.3'
}
```
If your content rating is Teen - 13+ years then you can integrate an additional Facebook Audience and Yandex networks:  
```gradle
dependencies {
      ...
      implementation 'com.facebook.android:audience-network-sdk:5.9.1'
      implementation 'com.yandex.android:mobileads:2.150'
      implementation 'com.yandex.android:mobmetricalib:3.13.3'
}
```

Or If your content rating is G - 0+ years General audiences then you can integrate an additional SuperAvesome network:  
```gradle
dependencies {
      ...
      implementation 'tv.superawesome.sdk.publisher:superawesome:7.2.6'
}
```

## Step 4 Add Cross Promotion SDK
Cross promotion is an app marketing strategy in which app developers promote one of their titles on another one of their titles. Cross promoting is especially effective for developers with large portfolios of games as a means to move users across titles and use the opportunity to scale each of their apps. This is most commonly used by hyper-casual publishers who have relatively low retention, and use cross promotion to keep users within their app portfolio.

Start your cross promotion campaign with CAS [here](https://cleveradssolutions.com).

```gradle
dependencies {
      ...
      implementation 'com.cleversolutions.ads:cas-promo:1.3.5'
}
```

## Step 5 Update AndroidManifest

### Manifest Permissions
Add the following permissions to your AndroidManifest.xml file inside the manifest tag but outside the <application> tag:
```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
   
    <!--Optional Permissions-->
    
    <!--This permission is used for certain ads that vibrate during play. 
    This is a normal level permission, so this permission just needs to be defined in the manifest to enable this ad feature.-->
    <uses-permission android:name="android.permission.VIBRATE" />
    
    <!--This permission is used for certain ads that allow the user to save a screenshot to their phone. 
    Note that with this permission on devices running Android 6.0 (API 23) or higher, 
    this permission must be requested from the user. 
    See Requesting Permissions for more details. https://developer.android.com/training/permissions/requesting -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    
    <!--This permission is not a mandatory permission, however, including it will enable accurate ad targeting-->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    ...
</manifest>
```
  
### Admob App ID
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
  </application>
</manifest>
```

### Add the CAS default settings file
Follow the [link](http://psvpromo.psvgamestudio.com/cas-settings.php) to download a cas_settings.json file.

Drop the cas_settings.json into the src/res/raw/ folder in your project.  

## Step 6 GDPR Managing Consent
CAS mediation platform supports publisher communication of a user’s consent choice to mediated networks.  

To use CAS API to update a user’s consent status, use this functions:  
```java
CAS.getSettings().setConsent(true);
```
If the user provided consent, please set the following flag to true:  
```java
CAS.getSettings().setConsent(false);
```
**It’s recommended to set the API prior to SDK Initialization.**

A detailed article on the use of user data can be found in the [Privacy Policy](/../../wiki/Privacy-Policy).

## Step 7 Verify Your Integration
The CAS SDK provides an easy way to verify that you’ve successfully integrated the ironSource SDK and any additional adapters; it also makes sure all required dependencies and frameworks were added for the various mediated ad networks.  
After you have finished your integration, call the following static method and confirm that all networks you have implemented are marked as VERIFIED:  
```java
CAS.validateIntegration(activity, appContentRating);
```
Find log information by tag: **CASIntegrationHelper**

Once you’ve successfully verified your integration, please remember to remove the integration helper from your code.

NOT. The Integration Helper tool reviews everything, including ad networks you may have intentionally chosen NOT to include in your application. These will appear as MISSING and there is no reason for concern. In the case the ad network’s integration has not been completed successfully, it will be marked as NOT VERIFIED.  

## Step 8 Initialize the SDK
You can access to SDK from any thread.  

Import the CAS SDK
```java
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
```

Configure Ads Settings singleton instance for configure all mediation managers:  
```java
CAS.getSettings().setConsent(userConsent);
CAS.getSettings().setNativeDebug(true);
// .. other settings
```

Initialize MediationManager instance:
```java
import com.cleversolutions.ads.android.CAS;
...
class YourActivity extends Activity{
  MediationManager manager;
  void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Configure AdsSettings before initialize
      manager = CAS.initialize(
            this,
            own_app_identifier, // or null when own_app_identifier matches the app package
            AdTypeFlags.Everything, // Set active Ad Types. 'AdTypeFlags.Banner or AdTypeFlags.Interstitial' for example.
            true // Demo ad fill only. Set FALSE for release!
      );
      // After initialization, advertising content is loading automatically.
  }
}
```
CAS.initialize can be called for different identifiers to create different managers. 

Optional. Subscribe listener to Ad Loading response:  
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

## Step 9 Implement our Ad Units
### Native Ads  
There will be support in the future.  

### Banner Ad
#### Add CASBannerView to the layout
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

#### You can alternatively create CASBannerView programmatically:
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
// OR same
bannerView.setSize(adaptiveSize);
```

### AdCallback
```java
// Executed when the ad is displayed.
// @param ad Information of displayed ad.
@MainThread void onShown(com.cleversolutions.ads.AdStatusHandler ad);

// Executed when the ad is failed to display.
// The Banner may automatically appear when the Ad is ready again.
// This will trigger the [onShown] callback again.
@MainThread void onShowFailed(String message);

// Executed when the user clicks on an ad.
@AnyThread void onClicked();

// Executed when the Ad is completed.
// Banner Ad does not use this callback.
@AnyThread void onComplete();

// Executed when the ad is closed.
// The Banner Ad cannot be displayed automatically after this callback for the current view.
// If you decide to show the Banner Ad on this view then you need refresh view visibility.
@AnyThread void onClosed();
```

### Check Ad Availability
You can ask for the ad availability directly by calling the following function:
```java
manager.isAdReady(AdType.Interstitial); //Check any AdType
```

### Show fullscreen Ad
Invoke the following method to serve an selected ad to your users:
```java
manager.show(
        AdType.Interstitial, // Ad type Interstitial or Rewarded
        new AdCallback(){...}, // AdCallback implementation or null
        null // AdNetwork name. Recomended use null for show high eCPM Ad.
      );
```

## Step 10 Adding App-ads txt file of our partners  
### "App-ads.txt: How to Make It & Why You Need It"

Last year, the ad tech industry struck back at one of its most elusive problems — widespread domain spoofing that let unauthorized developers sell premium inventory they didn’t actually have. The solution? Over two million developers adopted ads.txt — a simple-text public record of Authorized Digital Sellers for a particular publisher’s inventory — to make sure they didn’t lose money from DSPs and programmatic buyers who avoid noncompliant publishers. Thanks to buyers’ ability to [crawl ads.txt and verify seller authenticity](https://iabtechlab.com/ads-txt-about/), this has quickly become a standard for protecting brands. Ad fraud reduced by 11% in 2019 due to these efforts and publisher’s ability to implement more fraud prevention techniques.  

The time has come for ads.text to evolve in-app. The introduction of apps-ads.txt is an important method for mobile app devs to similarly eliminate fraud and improve transparency.

### What is app-ads.txt?

Like ads.txt, apps-ads.txt is a text file that app devs upload to their publisher website. It lists all ad sources authorized to sell that publisher’s inventory. [The IAB created a system](https://iabtechlab.com/press-releases/app-ads-txt-released-for-public-comment-as-next-step-to-fight-digital-advertising-inventory-fraud/) that allows buyers to distinguish the authorized sellers for specific in-app inventory, weeding out the undesirables.

### How does app-ads.txt work for mobile apps?

A DSP wanting to bid on an app’s inventory crawls the app-ads.txt file on a developer’s website to verify which ad sources are authorized to sell that app’s inventory. The DSP will only accept bid requests from ad sources listed on the file and authorized by the app developer.

### How does app-ads.txt help mobile app developers capture more ad revenue?

**Authorized in-app inventory**. An ever-increasing amount of brands are looking to advertise in-app today. Brand buyers now rely on an adherence to app-ads.txt to make sure they don’t buy unauthorized inventory from app developers and negatively impact campaign performance. Developers who don’t implement app-ads.txt can be removed from any brand buyer’s target media list. That’s why joining the app-ads.txt movement is crucial for publishers to maintain their revenue.

**Ad fraud prevention**. App-ads.txt blocks unauthorized developers who impersonate legitimate apps and mislead DSPs into spending brand budgets on fake inventory. With fraud instances minimized, authentic developers can retain more of the ad revenue from inventory genuinely targeted to their app.

### How do I create an app-ads.txt?

You must list your **Developer Website URL** in the GooglePlay and iTunes app stores. There must be a valid developer website URL in all app stores hosting your apps.

Make sure that your publisher website URL (not app specific URL)  is added in your app store listings. Advertising platforms will use this site to verify the app-ads.txt file.

We have made it easier for you to include CAS list of entries so that don’t have to construct it on your own. Please copy and paste the following text block and include in your txt file along with entries you may have from your other monetization partners:  

**[App-ads.txt](https://cleveradssolutions.com/app-ads.txt)** Updated 2.07.2020.  

**If your content rating G - 0+ years General audiences then please replace first line:**  
~~google.com, pub-7022579750037037, DIRECT, f08c47fec0942fa0~~  
to:  
```
google.com, pub-8858162097015410, DIRECT, f08c47fec0942fa0
```

## Mediation partners
* [Admob](https://admob.google.com/home)  
* [AppLovin](https://www.applovin.com)  
* [Chartboost](https://www.chartboost.com)  
* [KIDOZ](https://kidoz.net)  
* [UnityAds](https://unity.com/solutions/unity-ads)  
* [Vungle](https://vungle.com)  
* [AdColony](https://www.adcolony.com)  
* [StartApp](https://www.startapp.com)  
* [SuperAwesome](https://www.superawesome.com)  
* [IronSource](https://www.ironsrc.com)  
* [InMobi](https://www.inmobi.com)  
* [Facebook Audience](https://www.facebook.com/business/marketing/audience-network)  
* [Yandex Ad](https://yandex.ru/dev/mobile-ads)  

## Support
Site: [https://cleveradssolutions.com](https://cleveradssolutions.com)

Technical support: Max  
Skype: m.shevchenko_15  

Network support: Vitaly  
Skype: zanzavital  

mailto:support@cleveradssolutions.com  
