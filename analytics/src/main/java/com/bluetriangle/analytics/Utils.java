package com.bluetriangle.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Random;

/**
 * Utility methods
 */
final class Utils {

    /**
     * Return the string resource for the given key or null if not found.
     *
     * @param context application context
     * @param key     name of the identifier to look up
     * @return string resource for the given key or null if not found.
     */
    static String getResourceString(final Context context, final String key) {
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
    static boolean isDebuggable(final Context context) {
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
    static String generateRandomId() {
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
    static String getAppVersion(@NonNull final Context context) {
        final PackageInfo packageInfo = getAppPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "UNKNOWN";
    }

    static String getOs() {
        return String.format("Android %s", Build.VERSION.RELEASE);
    }

    static boolean isTablet(@NonNull final Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    static String getAppName(@NonNull final Context context) {
        final ApplicationInfo applicationInfo = context.getApplicationInfo();
        final int appNameStringResourceId = applicationInfo.labelRes;
        final String appName = appNameStringResourceId == 0 ? applicationInfo.nonLocalizedLabel.toString() :
                context.getString(appNameStringResourceId);
        return String.format("%s %s", appName, getOs());
    }

    static String getDeviceName() {
        if (Build.MODEL.startsWith(Build.MANUFACTURER)) {
            return capitalize(Build.MODEL);
        }
        return capitalize(Build.MANUFACTURER) + " " + Build.MODEL;
    }

    static String capitalize(@Nullable final String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Build the base 64 encoded data to POST to the API
     *
     * @return base 64 encoded JSON payload
     * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
     */
    static byte[] b64encode(final String data) throws UnsupportedEncodingException {
        return Base64.encode(data.getBytes(Constants.UTF_8), Base64.DEFAULT);
    }

    static String exceptionToStacktrace(@Nullable final String message, @NonNull Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        printWriter.close();

        final String[] lines = result.toString().split("\\r?\\n");
        final StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(message)) {
            sb.append(message);
            sb.append("~~");
        }
        for (int i = 0; i < lines.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!lines[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(lines[i]);
                sb.append("~~");
            }
        }
        sb.append(lines[lines.length - 1].trim());
        final String stacktrace = sb.toString();
        return stacktrace;
    }
}
