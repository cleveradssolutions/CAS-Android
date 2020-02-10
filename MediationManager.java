package com.cleversolutions.ads;

import com.cleversolutions.basement.ApplicationPauseListener;
import com.cleversolutions.basement.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MediationManager extends ApplicationPauseListener {
   /**
     * Event on ad types enabled processing.
     * Use [Event.add] for subscribe listener.
     */
   @NotNull
   Event<AdEnabledListener> getOnAdEnabledEvent();

    /**
     * Event on [AdType] load response.
     * Use [Event.add] for subscribe listener.
     */
   @NotNull
   Event<AdLoadCallback> getOnAdLoadEvent();

    /**
     * Event on Network unit status changed.
     * Use [Event.add] for subscribe listener.
     */
   @NotNull
   Event<AdStatusListener> getOnStatusChanged();

   /**
     * Get current enabled [AdTypeFlags] pattern.
     */
   @AdTypeFlagsDef int getEnabledAdFlags();

    /**
     * Get current [AdsSettings] for read/write.
     */
   @NotNull
   AdsSettings getSettings();

   /**
     * Is visible [AdType.Interstitial] or [AdType.Rewarded] right now.
     */
   boolean isFullscreenAdVisible();

   /**
     * Get banner ad size
     */
   @NotNull
   AdSize getBannerSize();

   /**
     * Set banner ad size
     */
   void setBannerSize(@NotNull AdSize size);

   /**
     * Force show ad by selected [type] and [network].
     * Subscribe [callback] to current shown.
     *
     * @param network Network name or null for show any ready net
     */
   void show(@NotNull AdType type, @Nullable AdCallback callback, @Nullable AdNetwork network);

   /**
     * Check selected [callback] is active for target [type]
     */
   boolean isValidCallback(@NotNull AdType type, @NotNull AdCallback callback);

   /**
     * Check ready ad [type]
     */
   boolean isAdReady(@NotNull AdType type);

   /**
     * Get last active mediation ad name of selected [type].
     * Can return Empty Sting.
     */
   @NotNull
   String getLastActiveMediation(@NotNull AdType type);

    /**
     * Set [enabled] ad [type] to processing.
     * The state will not be saved between sessions.
     */
   void setEnabled(@NotNull AdType type, boolean enabled);
    /**
     * Ad [type] is processing.
     */
   boolean isEnabled(@NotNull AdType type);
   
    /**
     * Set available ad types to processing of selected [types].
     * The state will not be saved between sessions.
     * For set available of all types use [AdTypeFlags.Everything]
     */
   void enableAdTypes(@AdTypeFlagsDef int types);

    /**
     * Set NOT available ad types to processing of selected [types].
     * The state will not be saved between sessions.
     * For set NOT available of all types use [AdTypeFlags.Everything]
     */
   void disableAdTypes(@AdTypeFlagsDef int types);

    /**
     * Hide all [AdType.Small] from the screen.
     * For hide specific banner only use View.setVisibility.
     */
   void hideBanner();

    /**
     * Enable manual call [onPause] and [onResume]
     * Default: false and state changed with change state main activity
     */
   void setManualPauseControl(boolean var1);

    /**
     * Pause [MediationManager] processing.
     * Can be paused automatically with change state main activity.
     * For control automatically mode use [setManualPauseControl]
     */
   void onPause();

    /**
     * Resume [MediationManager] processing.
     * Can be paused automatically with change state main activity.
     * For control automatically mode use [setManualPauseControl]
     */
   void onResume();
}
