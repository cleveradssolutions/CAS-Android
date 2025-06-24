## Google Ads Android Mediation Adapter Changelog
`com.cleveradssolutions:google:`  

### 24.4.0.1
- Fixed an issue where the adaptive banner did not fill the entire space under certain configurations. 
- Added Max ad content rating flag for Google Ads requests (Closed beta)

### 24.4.0.0
- Certified with Google Mobile Ads - 24.4.0
- Show Screen ads in Immersive mode to prevent the system bars from showing up.

### 24.2.0.0
- Certified with Google Mobile Ads - 24.2.0
- Added Native and AppOpen ad support

## Legacy Versions are incompatible with CAS 4.0

### 23.6.0.0
- Certified with Google Mobile Ads - 23.6.0

### 23.5.0.0
- Certified with Google Mobile Ads - 23.5.0

### 23.4.0.0
- Certified with Google Mobile Ads - 23.4.0

### 23.3.0.0
- Certified with Google Mobile Ads - 23.3.0

### 23.2.0.0
- Certified with Google Mobile Ads - 23.2.0

### 23.0.0.0
- Certified with Google Mobile Ads - 23.0.0
- Removed `MobileAdsInitProvider` from the AndroidManifest. 
The definition of `com.google.android.gms.ads.DELAY_APP_MEASUREMENT_INIT` in the manifest is no longer required.
CAS can initialize Google Mobile Ads later if necessary.

### 22.6.0.2
- Updated the minimum required CAS SDK version to 3.5.6.
- Minor performance improvements.

### 22.6.0.1
- Updated the minimum required CAS SDK version to 3.5.3.
- Improving the banner adaptive support.

### 22.6.0.0
- Updated the minimum required CAS SDK version to 3.5.1.
- Certified with Google Mobile Ads - 22.6.0

### 22.5.0.0
- Updated the minimum required CAS SDK version to 3.5.0.
- Certified with Google Mobile Ads - 22.5.0

### 22.4.0.1
- Updated the minimum required CAS SDK version to 3.4.1.
- Google Mobile Ads has been downgraded from 22.4.0 to 22.3.0 to avoid issues with Gradle Wrapper 4 version.

### 22.4.0.0
- Updated the minimum required CAS SDK version to 3.3.2.
- Certified with Google Mobile Ads - 22.4.0

### 22.3.0.1
- Updated the minimum required CAS SDK version to 3.3.0.

### 22.3.0.0
- Updated the minimum required CAS SDK version to 3.2.5.
- Certified with Google Mobile Ads - 22.3.0

### 22.2.0.2
- Updated the minimum required CAS SDK version to 3.2.3.
- Now Google Ads SDK will only be initialized when used in meidation.

### 22.2.0.1
- Updated the minimum required CAS SDK version to 3.2.2.
- Minor improvements.

### 22.2.0.0
- Updated the minimum required CAS SDK version to 3.2.0.
- Certified with Google Mobile Ads - 22.2.0

### 22.1.0.1
- Updated the minimum required CAS SDK version to 3.1.8.
- Fixed some issues with the Native Banner ad.

### 22.1.0.0
- Updated the minimum required CAS SDK version to 3.1.7.
- Certified with GoogleAds - 22.1.0
- Support `AdPaidCallback` in `CASAppOpen.contentCallback` to receive impression data.

### 22.0.0.0
- Updated the minimum required CAS SDK version to 3.1.3.
- Certified with Google Ads - 22.0.0

### 21.5.0.4
- Updated the minimum required CAS SDK version to 3.1.2.
- Improved revenue precision in impression event

### 21.5.0.3
- Updated the minimum required CAS SDK version to 3.1.1.
- Added Native Ad Units for use in Banner ad format.

### 21.5.0.0
- Updated the minimum required CAS SDK version to 3.0.1.
- Certified with Google Ads - 21.5.0

### 21.4.0.2
- Updated the minimum required CAS SDK version to 3.0.0.
- Increased precision of ad revenue value for automatic analytics collection.

### 21.4.0.1
- Updated the minimum required CAS SDK version to 2.9.9.
- Certified with Google Ads - 21.4.0

### 21.3.0.0
- Updated the minimum required CAS SDK version to 2.9.3.
- Certified with Google Ads - 21.3.0

### 21.1.0.1
- Updated the minimum required CAS SDK version to 2.8.5.
- Improved performance.

### 21.1.0.0
- Updated the minimum required CAS SDK version to 2.8.4.
- Certified with Google Ads - 21.1.0

### 21.0.0.1
- Updated the minimum required CAS SDK version to 2.8.3.
- Compatibility with CAS 2.8.3.

### 21.0.0.0
- Updated the minimum required CAS SDK version to 2.8.1.
- Certified with Google Ads - 21.0.0
- Fixed App Open Ads with `demo` identifier.
- Remove Child directed treatment unspecified specification.

### 20.6.0.1
- Updated the minimum required CAS SDK version to 2.7.2.
- Improved adapter performance.