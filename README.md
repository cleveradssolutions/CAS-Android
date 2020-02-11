# CleverAdsSolutions-Android

# SDK Integration

## Before You Start
We support Android Operating Systems Version 4.2 (API level 17) and up.  

## AndroidX
As of SDK 18.0.0, AdMob migrated from Android Support Libraries to Jetpack (AndroidX) Libraries. Refer to the [Google Play services release notes](https://developers.google.com/android/guides/releases#june_17_2019) for more information.  

Due to this, we working with the AdMob adapter it’s required that your project migrates from Android Support Libraries to Jetpack Libraries (Android X) if you are using any. Please refer to Migrating to AndroidX for more information.  

# Table of contents
 1.  [Add the CAS SDK to Your Project](#step-1-add-the-cas-sdk-to-your-project)  
 2.  [Add the CAS default settings file](#step-2-add-the-cas-default-settings-file)  
 3.  [Update AndroidManifest](#step-3-update-androidmanifest)  
 4.  [Add mediation SDK and support libraries](#step-4-add-mediation-sdk-and-support-libraries)  
 5.  [For ProGuard Users Only](#step-5-for-proguard-users-only)  
 6.  [GDPR Managing Consent](#step-6-gdpr-managing-consent)
 7.  [Initialize the SDK](#step-7-initialize-the-sdk)  
 8.  [Implement our Ad Units](#step-8-implement-our-ad-units)  
 9.  [Adding App-ads.txt file of our partners](#step-9-adding-app-ads-txt-file-of-our-partners)  

## Step 1 Add the CAS SDK to Your Project

### Gradle
There will be support in the future.  

### Manual Download
- Download the latest release binaries from GitHub, specifically [CleverAdsSolutions.aar](https://github.com/cleveradssolutions/CAS-Android/releases/latest)
- Create or open your existing Android project in Android Studio.
- Add new module and import CleverAdsSolutions.aar. Name the module "CleverAdsSolutions" for example.
- Open Module Settings for the app and add "CleverAdsSolutions" module as a dependency.  

## Step 2 Add the CAS default settings file
Follow the [link](http://psvpromo.psvgamestudio.com/cas-settings.php) to download a cas_settings.json file.

Drop the cas_settings.json into the src/res/raw/ folder in your project.  

## Step 3 Update AndroidManifest

### Manifest Permissions
Add the following permissions to your AndroidManifest.xml file inside the manifest tag but outside the <application> tag:
```xml
<manifest>
  <application>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    ...
  </application>
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

## Step 4 Add mediation SDK and support libraries
Add the following to your app’s **build.gradle** file inside repositories section:
```gradle
repositories {
      google()
      jcenter()
      maven {
          url "https://adcolony.bintray.com/AdColony"
      }
      maven {
          url 'http://dl.bintray.com/gabrielcoman/maven' // SuperAwesome
      }
      maven {
          url "http://dl.bintray.com/superawesome/SuperAwesomeSDK"
      }
      maven {
          url "https://dl.bintray.com/ironsource-mobile/android-sdk"
      }
  }
```
Then add following in [dependencies.md](dependencies.md) to the dependencies section.  

## Step 5 For ProGuard Users Only
If you are using ProGuard, you must add the [following code](proguard-rules.pro) to your ProGuard file (Android Studio: proguard-rules.pro or Eclipse: proguard-project.txt).  

## Step 6 GDPR Managing Consent
CAS mediation platform supports publisher communication of a user’s consent choice to mediated networks.  

To use ironSource’s API to update a user’s consent status, use this functions:  
```java
CAS.getSettings().setConsent(true);
```
If the user provided consent, please set the following flag to true:  
```java
CAS.getSettings().setConsent(false);
```
**It’s recommended to set the API prior to SDK Initialization.**

## Step 7 Initialize the SDK
Create [MediationManager](/MediationManager.java) instance:

```java
import com.cleversolutions.ads.android.CAS;
...
class YourActivity extends Activity{
  MediationManager manager;
  void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // Configure mediation before initialize 
      AdsSettings settings = CAS.getSettings();
      settings.setConsent(false);
      
      // Do initialize MediationManager instance with CAS.getSettings()
      manager = CAS.initialize(
            this,
            own_app_identifier, // or null when own_app_identifier matches the app package
            AdTypeFlags.Everything
      );
  }
}
```

## Step 8 Implement our Ad Units
### Native Ads  
There will be support in the future.  

### Banner
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
bannerView.setSize(AdSize.Standard320x50);
bannerView.setPosition(AdPosition.Center);
bannerView.setListener(new AdCallback(){...});
// TODO: Add bannerView to your view hierarchy.
```

### AdCallback
```java
// Executed when the user clicks on an ad.
void onClicked();

// Executed when the interstitial ad is closed.
void onClosed();

// Executed when the ad is completed. Used for Rewarded Ad only.
void onComplete();

// Executed when the ad is failed to display.
void onShowFailed(String message);

// Executed when the ad is displayed.
void onShown(com.cleversolutions.ads.AdStatusHandler ad);
```

### Check Ad Availability
You can ask for the ad availability directly by calling the following function:
```java
manager.isAdReady(AdType.Interstitial);
```

### Show Ad
Invoke the following method to serve an selected ad to your users:
```java
manager.show(
        AdType.Interstitial, // Ad type Interstitial or Rewarded
        new AdCallback(){...}, // AdCallback implementation or null
        null // AdNetwork name. Recomended use null for show high eCPM Ad.
      );
```

## Step 9 Adding App-ads txt file of our partners  
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
**[App-ads.txt](https://cleveradssolutions.com/app-ads.txt)**
