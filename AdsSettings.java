package com.cleversolutions.ads;

import com.cleversolutions.basement.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AdsSettings {
    /**
     * Used App ID. Can be read only after [MediationManager] initialize.
     */
   @NotNull
   String getAppID();

   @NotNull
   Event<OptionsListener> getOptionChangedEvent();

   @NotNull
   Event<FilterListener> getFilterChangedEvent();

   @Nullable
   String getPluginPlatformName();

   void setPluginPlatformName(@Nullable String name);

   @Nullable
   String getPluginPlatformVersion();

   void setPluginPlatformVersion(@Nullable String version);

    /**
     * Is enabled CAS analytics collection  for this app on this device.
     * This setting is persisted across app sessions.
     * Analytics services: Firebase Analytics
     * By default it is disabled.
     */
   boolean getAnalyticsCollectionEnabled();

    /**
     * Sets CAS analytics collection is enabled for this app on this device.
     * This setting is persisted across app sessions.
     * Analytics services: Firebase Analytics
     * By default it is disabled.
     */
   void setAnalyticsCollectionEnabled(boolean var1);
    
    /**
     * Interval display banner between load next ad in seconds.
     * Default: 30 seconds.
     */
   int getBannerRefreshInterval();

    /**
     * Interval display banner between load next ad in seconds.
     * Default: 30 seconds.
     */
   void setBannerRefreshInterval(int seconds);

    /**
     * GDPR user Consent SDK Implementation for ads on session.
     * Default: false
     */
   boolean getConsent();

    /**
     * GDPR user Consent SDK Implementation for ads on session.
     * Default: false
     */
   void setConsent(boolean consent);

    /**
     * Is wait [consent] call to begin initialize ADS.
     */
   boolean isWaitConsentToInitialize();

    /**
     * Wait [consent] call to begin initialize ADS.
     * Return true only before initialize ads.
     * Default: flase
     *
     * Throws [IllegalArgumentException] on change value after initialization.
     */
   void setWaitConsentToInitialize(boolean waitConsent);

    /**
     * Debug log mode for native mediation SDK.
     * Default: false
     */
   boolean getNativeDebug();

    /**
     * Debug log mode for native mediation SDK.
     * Default: false
     */
   void setNativeDebug(boolean debug);

    /**
     * Muted sounds in ads
     * Default: true
     */
   boolean getMutedAdSounds();

    /**
     * Muted sounds in ads
     * Default: true
     */
   void setMutedAdSounds(boolean mute);

    /**
     * Mediation loading manager mode.
     * Default: [LoadingManagerMode.Optimal]
     */
   @NotNull
   LoadingManagerMode getLoadingMode();

    /**
     * Mediation loading manager mode.
     * Default: [LoadingManagerMode.Optimal]
     */
   void setLoadingMode(@NotNull LoadingManagerMode mode);

    /**
     * Setting a maximum ad content rating
     * Some types of ads may be more suitable for your app’s audience than others.
     * Showing users ads that are a better fit can improve their overall ad experience and help maximize your app’s revenue.
     * That rating will override any rating set in the AdMob user interface.
     * Default: [ContentRating.None] for use network settings
     */
   @NotNull
   ContentRating getContentRating();

    /**
     * Setting a maximum ad content rating
     * Some types of ads may be more suitable for your app’s audience than others.
     * Showing users ads that are a better fit can improve their overall ad experience and help maximize your app’s revenue.
     * That rating will override any rating set in the AdMob user interface.
     * Default: [ContentRating.None] for use network settings
     */
   void setContentRating(@NotNull ContentRating var1);

    /**
     * Is enabled forces sdk to filter ads with violence, drugs, etc
     * Default: false
     */
   boolean isTaggedForChildren();

    /**
     * Set enabled forces sdk to filter ads with violence, drugs, etc
     * Default: false
     */
   void setTaggedForChildren(boolean var1);

   public interface OptionsListener {
      /**
       * Update user Consent status and on [AdsSettings.consent].
       */
      void onConsentChanged(boolean var1);

      /**
       * Update debug log mode and on  [AdsSettings.nativeDebug]
       */
      void onNativeDebugChanged(boolean var1);

      /**
       *  Update Mute ad sounds on [AdsSettings.mutedAdSounds]
       */
      void onMuteAdSoundsChanged(boolean var1);
   }

   public interface FilterListener {
      /**
       *  Update Mute ad sounds on [AdsSettings.isTaggedForChildren]
       */
      void onTaggedForChildrenChanged(boolean var1);

      /**
       *  Update Mute ad sounds on [AdsSettings.contentRating]
       */
      void onRatingFilterChanged(@NotNull ContentRating var1);
   }
}
