## CASExchange Android Mediation Adapter Changelog
```kotlin
implementation("com.cleveradssolutions:cas-exchange:4.5.2")
```

### 4.5.2
- Fixed a rare issue with impression event URL for Interstitial.
- Requires CAS 4.5.4+ due to a known issue.

### 4.5.0
- Fixed Google Play transition handling.
- Added Intent URL support.
- Added url parameter `cas_open_in_browser` to open in external browser.

### 4.3.1
- Fixed rare crash with `NullPointerException` from Interstitial ad activity. Issue [#30](https://github.com/cleveradssolutions/CAS-Android/issues/30)
- Fixed ILRD collection for Native ads.

### 4.3.0
- Added Native ad support

### 4.1.0
- Fix vast regex to accept single quotes

### 4.0.2
- Improved ad content requests.

## Legacy Versions are incompatible with CAS 4.0

### 3.9.8
- Fixed rendering of some creatives.

### 3.9.7
- Update endpoint.

### 3.9.6
- Update endpoint.

### 3.9.4
- Added CAS marker for all advertising formats.
- Enabled mute sounds button for Video ads.

### 3.9.3
- Fixed critical ANR from Adapter initialization.

### 3.9.1
- Reorganization of internal resources

### 3.9.0
- Minor internal improvements

### 3.8.0
- Init version
