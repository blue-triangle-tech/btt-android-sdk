package com.bluetriangle.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Random;

/**
 * Utility methods
 */
public class Utils {

    /**
     * Return the string resource for the given key or null if not found.
     *
     * @param context application context
     * @param key     name of the identifier to look up
     * @return string resource for the given key or null if not found.
     */
    protected static String getResourceString(final Context context, final String key) {
        final int id = getIdentifier(context, "string", key);
        if (id != 0) {
            return context.getResources().getString(id);
        } else {
            return null;
        }
    }

    /**
     * Get the identifier for the resource with a given type and key.
     *
     * @param context application context
     * @param type    resource type, ex: string
     * @param key     name of the identifier to look up
     * @return resource identifier for requested type and key or 0 if not found
     */
    private static int getIdentifier(final Context context, final String type, final String key) {
        return context.getResources().getIdentifier(key, type, context.getPackageName());
    }

    /**
     * Determine if this application is debuggable
     *
     * @param context application context
     * @return true if application flag for debugging is set, else false
     */
    protected static boolean isDebuggable(final Context context) {
        try {
            final String packageName = context.getPackageName();
            final int flags = context.getPackageManager().getApplicationInfo(packageName, 0).flags;
            return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    /**
     * Returns a randomly generated ID that is 18 characters long
     *
     * @return random long
     */
    public static String generateRandomId() {
        final long random = Math.abs((new Random()).nextLong());
        return String.format("%019d", random).substring(0, 19);
    }

    /**
     * Get the applications package info
     *
     * @param context application context
     * @return application's package info
     */
    private static PackageInfo getAppPackageInfo(@NonNull final Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        } catch (PackageManager.NameNotFoundException ignore) {
            // this should never happen, we are looking up ourself
        }
        return null;
    }

    /**
     * Get the application's version
     *
     * @param context application context
     * @return The application's version or UNKNOWN if not found
     */
    protected static String getAppVersion(@NonNull final Context context) {
        final PackageInfo packageInfo = getAppPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "UNKNOWN";
    }

    public static String getDeviceName() {
        if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            return capitalize(Build.MODEL);
        }
        return capitalize(Build.MANUFACTURER) + " " + Build.MODEL;
    }

    public static String capitalize(@Nullable final String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
