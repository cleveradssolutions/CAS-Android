package com.cleversolutions.ads.android;


import android.app.Activity;
import androidx.annotation.RequiresPermission;
import com.cleversolutions.ads.AdsSettings;
import com.cleversolutions.ads.MediationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CAS {

    /**
     * Get singleton instance for configure all mediation managers
     */
   @NotNull
   public static final AdsSettings getSettings();

    /**
     * Get last initialized [MediationManager]
     */
   @Nullable
   public static final MediationManager getManager();

    /**
     * Initialize new [MediationManager] and save in [manager] field.
     * Can be called for different identifiers to create different managers.
     * After initialization, advertising content of [enabledAdTypes] is loading automatically.
     *
     * @param activity Main Activity
     * @param managerID Own manager identifier or null when matches the app package
     * @param enabledAdTypes Enabled Ad types for loading automatically
     */
   @RequiresPermission(
      allOf = {"android.permission.ACCESS_NETWORK_STATE", "android.permission.INTERNET", "android.permission.ACCESS_WIFI_STATE"}
   )
   @NotNull
   public static final MediationManager initialize(@NotNull Activity activity, @Nullable String managerID, int enabledAdTypes);
   
 }
