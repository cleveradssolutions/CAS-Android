# CleverAdsSolutions-Android

# SDK Integration

## Before You Start
We support Android Operating Systems Version 4.2 (API level 17) and up.

## AndroidX
As of SDK 18.0.0, AdMob migrated from Android Support Libraries to Jetpack (AndroidX) Libraries. Refer to the [Google Play services release notes](https://developers.google.com/android/guides/releases#june_17_2019) for more information.  

Due to this, we working with the AdMob adapter it’s required that your project migrates from Android Support Libraries to Jetpack Libraries (Android X) if you are using any. Please refer to Migrating to AndroidX for more information.  

## Step 1. Add the CAS SDK to Your Project

### Gradle
Coming soon!

### Manual Download
- Download the latest release binaries from GitHub, specifically [CleverAdsSolutions.aar](https://github.com/cleveradssolutions/CAS-Android/releases/latest)
- Create or open your existing Android project in Android Studio.
- Add new module and import CleverAdsSolutions.aar. Name the module "CleverAdsSolutions" for example.
- Open Module Settings for the app and add "CleverAdsSolutions" module as a dependency.

## Step 2. Add the CAS default settings file
Follow the [link](http://psvpromo.psvgamestudio.com/cas-settings.php) to download a cas_settings.json file.

Drop the cas_settings.json into the src/res/raw/ folder in your project.  

## Step 3. Update AndroidManifest.xml

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

## Step 4. Add mediation SDK and support libraries
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

## Step 5. For ProGuard Users Only
If you are using ProGuard, you must add the [following code](proguard-rules.pro) to your ProGuard file (Android Studio: proguard-rules.pro or Eclipse: proguard-project.txt)

## Step 6. Initialize the SDK
```java
import com.cleversolutions.ads.android.CAS;
...
class YourActivity extends Activity{
  MediationManager manager;
  void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      manager = CAS.initialize(
            this,
            own_app_identifier, // or null when own_app_identifier matches the app package
            new AdsSettings(), // or null for select previous/default settings
            AdTypeFlags.Everything
      );
  }
}
```

## Step 7. Implement our Ad Units
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
