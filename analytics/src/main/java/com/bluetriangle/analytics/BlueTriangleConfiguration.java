package com.bluetriangle.analytics;

import android.util.Log;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlueTriangleConfiguration {

    public static final String DEFAULT_TRACKER_URL = "https://d.btttag.com/analytics.rcv";
    public static final String DEFAULT_ERROR_REPORTING_URL = "https://d.btttag.com/err.rcv";

    /**
     * The Blue Triangle site ID
     */
    @Nullable private String siteId;

    /**
     * Global User ID
     */
    @Nullable private String globalUserId;

    /**
     * Session ID
     */
    @Nullable private String sessionId;

    /**
     * the application's name
     */
    @Nullable private String applicationName;

    @Nullable private String userAgent;

    private boolean debug = false;
    private int debugLevel = Log.DEBUG;
    @Nullable private Logger logger = NoOpLogger.getInstance();

    @NonNull private String trackerUrl = DEFAULT_TRACKER_URL;
    @NonNull private String errorReportingUrl = DEFAULT_ERROR_REPORTING_URL;

    /**
     * Cache directory path
     */
    private String cacheDirectory = null;

    /**
     * Max items in the cache
     */
    private int maxCacheItems = 100;

    /**
     * Max attmepts to resend a payload
     */
    private int maxAttempts = 3;

    /**
     * the instance to cache payloads
     */
    private PayloadCache payloadCache = null;

    private boolean trackCrashesEnabled = false;
    private boolean performanceMonitorEnabled = true;
    private long performanceMonitorIntervalMs = TimeUnit.SECONDS.toMillis(1);

    @Nullable
    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(@Nullable String siteId) {
        this.siteId = siteId;
    }

    @Nullable
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(@Nullable String sessionId) {
        this.sessionId = sessionId;
    }

    @Nullable
    public String getGlobalUserId() {
        return globalUserId;
    }

    public void setGlobalUserId(@Nullable String globalUserId) {
        this.globalUserId = globalUserId;
    }

    @Nullable
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(@Nullable String userAgent) {
        this.userAgent = userAgent;
    }

    @Nullable
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(@Nullable String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * get the cache directory path
     *
     * @return cache directory path
     */
    public String getCacheDirectory() {
        return cacheDirectory;
    }

    /**
     * Set the cache directory path
     *
     * @param cacheDirectory path to cache director
     */
    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public int getMaxCacheItems() {
        return maxCacheItems;
    }

    public void setMaxCacheItems(int maxCacheItems) {
        this.maxCacheItems = maxCacheItems;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public PayloadCache getPayloadCache() {
        if (payloadCache == null) {
            payloadCache = new PayloadCache(this);
        }
        return payloadCache;
    }

    public void setPayloadCache(PayloadCache payloadCache) {
        this.payloadCache = payloadCache;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    @NonNull
    public Logger getLogger() {
        if (logger == null) {
            if (debug) {
                logger = new AndroidLogger(debugLevel);
            } else {
                logger = NoOpLogger.getInstance();
            }
        }
        return logger;
    }

    public void setLogger(@NonNull Logger logger) {
        this.logger = logger;
    }

    @NonNull
    public String getTrackerUrl() {
        return trackerUrl;
    }

    public void setTrackerUrl(@NonNull String trackerUrl) {
        this.trackerUrl = trackerUrl;
    }

    @NonNull
    public String getErrorReportingUrl() {
        return errorReportingUrl;
    }

    public void setErrorReportingUrl(@NonNull String errorReportingUrl) {
        this.errorReportingUrl = errorReportingUrl;
    }

    public boolean isPerformanceMonitorEnabled() {
        return performanceMonitorEnabled;
    }

    public void setPerformanceMonitorEnabled(boolean performanceMonitorEnabled) {
        this.performanceMonitorEnabled = performanceMonitorEnabled;
    }

    public long getPerformanceMonitorIntervalMs() {
        return performanceMonitorIntervalMs;
    }

    public void setPerformanceMonitorIntervalMs(long performanceMonitorIntervalMs) {
        this.performanceMonitorIntervalMs = performanceMonitorIntervalMs;
    }

    public boolean isTrackCrashesEnabled() {
        return trackCrashesEnabled;
    }

    public void setTrackCrashesEnabled(boolean trackCrashesEnabled) {
        this.trackCrashesEnabled = trackCrashesEnabled;
    }
}
