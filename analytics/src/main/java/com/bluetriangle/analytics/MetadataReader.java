package com.bluetriangle.analytics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.TextUtilsCompat;

final class MetadataReader {

    private static final String SITE_ID = "com.blue-triangle.site-id";
    private static final String DEBUG = "com.blue-triangle.debug";
    private static final String DEBUG_LEVEL = "com.blue-triangle.debug.level";
    private static final String MAX_CACHE_ITEMS = "com.blue-triangle.cache.max-items";
    private static final String MAX_RETRY_ATTEMPTS = "com.blue-triangle.cache.max-retry-attempts";
    private static final String PERFORMANCE_MONITOR_ENABLE = "com.blue-triangle.performance-monitor.enable";
    private static final String PERFORMANCE_MONITOR_INTERVAL = "com.blue-triangle.performance-monitor.interval-ms";
    private static final String TRACK_CRASHES_ENABLE = "com.blue-triangle.track-crashes.enable";

    private MetadataReader() {
    }

    static void applyMetadata(@NonNull final Context context, @NonNull final BlueTriangleConfiguration configuration) {
        try {
            final Bundle metadata = getMetadata(context);
            if (metadata != null) {
                final String siteId = readString(metadata, SITE_ID, null);
                if (TextUtils.isEmpty(siteId)) {
                    configuration.getLogger().error("No site ID");
                }
                configuration.setSiteId(siteId);

                configuration.setDebug(readBool(metadata, DEBUG, configuration.isDebug()));
                configuration.setDebugLevel(readInt(metadata, DEBUG_LEVEL, configuration.getDebugLevel()));

                configuration.setMaxCacheItems(readInt(metadata, MAX_CACHE_ITEMS, configuration.getMaxCacheItems()));
                configuration.setMaxAttempts(readInt(metadata, MAX_RETRY_ATTEMPTS, configuration.getMaxAttempts()));

                configuration.setPerformanceMonitorEnabled(
                        readBool(metadata, PERFORMANCE_MONITOR_ENABLE, configuration.isPerformanceMonitorEnabled()));

                configuration.setPerformanceMonitorIntervalMs(
                        readLong(metadata, PERFORMANCE_MONITOR_INTERVAL,
                                configuration.getPerformanceMonitorIntervalMs()));

                configuration.setTrackCrashesEnabled(
                        readBool(metadata, TRACK_CRASHES_ENABLE, configuration.isTrackCrashesEnabled()));
            }
        } catch (Throwable e) {
            configuration.getLogger().error(e, "Error reading metadata configuration");
        }
    }


    private static @Nullable Bundle getMetadata(@NonNull final Context context) throws PackageManager.NameNotFoundException {
        final ApplicationInfo app = context.getPackageManager()
                .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        return app.metaData;
    }

    private static boolean readBool(final @NonNull Bundle metadata, final @NonNull String key,
            final boolean defaultValue) {
        final boolean value = metadata.getBoolean(key, defaultValue);
        return value;
    }

    private static @Nullable
    String readString(final @NonNull Bundle metadata, final @NonNull String key, final @Nullable String defaultValue) {
        final String value = metadata.getString(key, defaultValue);
        return value;
    }

    private static int readInt(final @NonNull Bundle metadata, final @NonNull String key, final int defaultValue) {
        final int value = metadata.getInt(key, defaultValue);
        return value;
    }

    private static long readLong(final @NonNull Bundle metadata, final @NonNull String key, final long defaultValue) {
        // manifest meta-data only reads int if the value is not big enough
        final long value = metadata.getInt(key, (int) defaultValue);
        return value;
    }
}
