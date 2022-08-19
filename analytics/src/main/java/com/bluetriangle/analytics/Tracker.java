package com.bluetriangle.analytics;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * The tracker is a global object responsible for taking submitted timers and reporting them to the cloud server via a
 * background thread.
 */
public class Tracker {

    private static final String SHARED_PREFERENCES_NAME = "BTT_SHARED_PREFERENCES";

    /**
     * String resource name for the site ID
     */
    private static final String SITE_ID_RESOURCE_KEY = "btt_site_id";

    /**
     * Singleton instance of the tracker
     */
    private static Tracker instance;

    /**
     * Weak reference to Android application context
     */
    private final WeakReference<Context> context;

    /**
     * A weak reference to the most recently started timer
     */
    private WeakReference<Timer> mostRecentTimer;

    /**
     * The tracker's configuration
     */
    private final BlueTriangleConfiguration configuration;

    /**
     * A map of fields that should be applied to all timers such as
     */
    final Map<String, String> globalFields;

    /**
     * Executor service to queue and submit timers
     */
    private final TrackerExecutor trackerExecutor;

    /**
     * Performance monitoring threads
     */
    private final HashMap<Long, PerformanceMonitor> performanceMonitors = new HashMap<>();

    /**
     * Initialize the tracker with default tracker URL and Site ID from string resources.
     *
     * @param context application context
     * @return the initialized tracker
     */
    public static Tracker init(@NonNull final Context context) {
        return init(context, null, null);
    }

    /**
     * Initialize the tracker with default tracker URL and given Site ID
     *
     * @param context application context
     * @param siteId  Site ID to send with all timers
     * @return the initialized tracker
     */
    public static Tracker init(@NonNull final Context context, @Nullable final String siteId) {
        return init(context, siteId, null);
    }

    /**
     * Initialize the tracker with given tracker URL and site ID
     *
     * @param context    application context
     * @param siteId     Site ID to send with all timers
     * @param trackerUrl the URL to submit timer data
     * @return the initialized tracker
     */
    synchronized public static Tracker init(@NonNull final Context context, @Nullable final String siteId,
            @Nullable final String trackerUrl) {
        if (instance != null) {
            return instance;
        }

        final BlueTriangleConfiguration configuration = new BlueTriangleConfiguration();
        MetadataReader.applyMetadata(context, configuration);

        configuration.setApplicationName(Utils.getAppNameAndOs(context));
        configuration.setUserAgent(Utils.buildUserAgent(context));

        final File cacheDir = new File(context.getCacheDir(), "bta");
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdir()) {
                configuration.getLogger().error("Error creating cache directory: %s", cacheDir.getAbsolutePath());
            }
        }
        configuration.setCacheDirectory(cacheDir.getAbsolutePath());

        if (!TextUtils.isEmpty(siteId)) {
            configuration.setSiteId(siteId);
        }

        if (!TextUtils.isEmpty(trackerUrl)) {
            configuration.setTrackerUrl(trackerUrl);
        }

        // if site id is still not configured, try legacy resource string method
        if (TextUtils.isEmpty(configuration.getSiteId())) {
            final String resourceSiteID = Utils.getResourceString(context, SITE_ID_RESOURCE_KEY);
            if (!TextUtils.isEmpty(resourceSiteID)) {
                configuration.setSiteId(resourceSiteID);
            }
        }

        if (configuration.isDebug()) {
            configuration.setLogger(new AndroidLogger(configuration.getDebugLevel()));
        }

        instance = new Tracker(context, configuration);
        return instance;
    }

    /**
     * Gets the singleton tracker instance to submit timers to. If not setup via the builder, it will be null.
     *
     * @return Singleton tracker instance or null if not built yet.
     */
    public static Tracker getInstance() {
        return instance;
    }

    private Tracker(@NonNull final Context context, @NonNull final BlueTriangleConfiguration configuration) {
        super();
        this.context = new WeakReference<>(context);
        this.configuration = configuration;
        globalFields = new HashMap<>(8);
        globalFields.put(Timer.FIELD_SITE_ID, configuration.getSiteId());
        globalFields.put(Timer.FIELD_BROWSER, Constants.BROWSER);
        final String os = Utils.getOs();
        final String appVersion = Utils.getAppVersion(context);
        final boolean isTablet = Utils.isTablet(context);
        globalFields.put(Timer.FIELD_DEVICE, isTablet ? "Tablet" : "Mobile");
        globalFields.put(Timer.FIELD_BROWSER_VERSION, String.format("%s-%s-%s", Constants.BROWSER, appVersion, os));
        globalFields.put(Timer.FIELD_SDK_VERSION, BuildConfig.SDK_VERSION);

        final String globalUserId = getOrCreateGlobalUserId();
        final String sessionId = Utils.generateRandomId();
        setSessionId(sessionId);
        configuration.setSessionId(sessionId);
        setGlobalUserId(globalUserId);
        configuration.setGlobalUserId(globalUserId);

        this.trackerExecutor = new TrackerExecutor(configuration);

        if (configuration.isTrackCrashesEnabled()) {
            trackCrashes();
        }
    }

    public @Nullable
    ActivityManager getActivityManager() {
        final Context ctx = context.get();
        if (ctx != null) {
            return (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        }
        return null;
    }

    synchronized void setMostRecentTimer(@NonNull final Timer timer) {
        mostRecentTimer = new WeakReference<>(timer);
    }

    @Nullable
    Timer getMostRecentTimer() {
        if (mostRecentTimer != null) {
            return mostRecentTimer.get();
        }
        return null;
    }

    /**
     * Get the stored global user ID or generate and persist if not found
     *
     * @return the global user ID
     */
    private String getOrCreateGlobalUserId() {
        String globalUserId = null;
        final Context context = this.context.get();
        if (context == null) {
            return Utils.generateRandomId();
        }

        final SharedPreferences sharedPreferences = context
                .getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Timer.FIELD_GLOBAL_USER_ID)) {
            globalUserId = sharedPreferences.getString(Timer.FIELD_GLOBAL_USER_ID, null);
        }

        if (TextUtils.isEmpty(globalUserId)) {
            globalUserId = Utils.generateRandomId();
            sharedPreferences.edit().putString(Timer.FIELD_GLOBAL_USER_ID, globalUserId).apply();
        }

        return globalUserId;
    }

    @NonNull
    PerformanceMonitor createPerformanceMonitor() {
        final PerformanceMonitor performanceMonitor = new PerformanceMonitor(configuration);
        performanceMonitors.put(performanceMonitor.getId(), performanceMonitor);
        return performanceMonitor;
    }

    @Nullable
    PerformanceMonitor getPerformanceMonitor(final long id) {
        return performanceMonitors.get(id);
    }

    void clearPerformanceMonitor(final long id) {
        performanceMonitors.remove(id);
    }

    /**
     * Submit a timer to the tracker to be sent to the cloud server.
     * <p>
     * If the timer has not been ended, it will be ended on submit.
     *
     * @param timer The timer to submit
     */
    public void submitTimer(@NonNull final Timer timer) {
        if (!timer.hasEnded()) {
            timer.end();
        }

        timer.setFields(globalFields);

        trackerExecutor.submit(new TimerRunnable(configuration, timer));
    }

    /**
     * Submit a cached payload to the executor
     * @param payload cached payload to retry sending
     */
    void submitPayload(@NonNull final Payload payload) {
        trackerExecutor.submit(new PayloadRunnable(configuration, payload));
    }

    /**
     * set the current session ID for this tracker
     *
     * @param sessionId session ID to send with all timers submitted
     */
    public void setSessionId(@NonNull final String sessionId) {
        setGlobalField(Timer.FIELD_SESSION_ID, sessionId);
    }

    /**
     * set the global user ID for this tracker
     *
     * @param globalUserId the global user ID to send with all timers submitted
     */
    public void setGlobalUserId(@NonNull final String globalUserId) {
        setGlobalField(Timer.FIELD_GLOBAL_USER_ID, globalUserId);
    }

    /**
     * Set this session's AB test identifier
     *
     * @param abTestIdentifier the AB test id
     */
    public void setSessionAbTestIdentifier(@NonNull final String abTestIdentifier) {
        setGlobalField(Timer.FIELD_AB_TEST_ID, abTestIdentifier);
    }

    /**
     * Set this session's data center value
     *
     * @param dataCenter the value for the data center
     */
    public void setSessionDataCenter(@NonNull final String dataCenter) {
        setGlobalField(Timer.FIELD_DATACENTER, dataCenter);
    }

    /**
     * Set this session's traffic segment name
     *
     * @param trafficSegmentName name of the traffic segment for this session
     */
    public void setSessionTrafficSegmentName(@NonNull final String trafficSegmentName) {
        setGlobalField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, trafficSegmentName);
    }

    /**
     * Set this session's campaign name
     *
     * @param campaignName name of campaign
     */
    public void setSessionCampaignName(@NonNull final String campaignName) {
        setGlobalField(Timer.FIELD_CAMPAIGN_NAME, campaignName);
    }

    /**
     * Set this session's campaign source
     *
     * @param campaignSource source of campaign
     */
    public void setSessionCampaignSource(@NonNull final String campaignSource) {
        setGlobalField(Timer.FIELD_CAMPAIGN_SOURCE, campaignSource);
    }

    /**
     * Set this session's campaign medium
     *
     * @param campaignMedium medium of campaign
     */
    public void setSessionCampaignMedium(@NonNull final String campaignMedium) {
        setGlobalField(Timer.FIELD_CAMPAIGN_MEDIUM, campaignMedium);
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, @NonNull final String value) {
        synchronized (globalFields) {
            globalFields.put(fieldName, value);
        }
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, final int value) {
        setGlobalField(fieldName, Integer.toString(value));
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, final double value) {
        setGlobalField(fieldName, Double.toString(value));
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, final float value) {
        setGlobalField(fieldName, Float.toString(value));
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, final boolean value) {
        setGlobalField(fieldName, Boolean.toString(value));
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    public void setGlobalField(@NonNull final String fieldName, final long value) {
        setGlobalField(fieldName, Long.toString(value));
    }

    /**
     * Get the current value of a global field
     *
     * @param fieldName the name of the field to get
     * @return the value of the given field name or null if doesn't exist
     */
    public String getGlobalField(@NonNull final String fieldName) {
        return globalFields.get(fieldName);
    }

    /**
     * Clear a global field from being set on all trackers
     *
     * @param fieldName the name of the field to remove
     */
    public void clearGlobalField(@NonNull final String fieldName) {
        synchronized (globalFields) {
            globalFields.remove(fieldName);
        }
    }

    public void trackCrashes() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof BtCrashHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new BtCrashHandler(configuration));
        }
    }

    /**
     * Manually track an error exception caught by your code
     * @param message optional message included with the stack trace
     * @param exception the exception to track
     */
    public void trackException(@Nullable final String message, @NonNull final Throwable exception) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        final Timer crashHitsTimer = new Timer().start();
        final String stacktrace = Utils.exceptionToStacktrace(message, exception);
        trackerExecutor.submit(new CrashRunnable(configuration, stacktrace, timeStamp, crashHitsTimer));
    }

    /**
     * Get the Blue Triangle configuration
     *
     * @return the configuration
     */
    public BlueTriangleConfiguration getConfiguration() {
        return configuration;
    }

    public void raiseTestException() {
        int a = 10, b = 0;
        System.out.println(a / b);
    }

}
