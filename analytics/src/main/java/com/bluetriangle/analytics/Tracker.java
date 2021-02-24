package com.bluetriangle.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

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
     * Default URL to submit the timer data to
     * old url https://d.btttag.com/btt.gif
     */
    //http://3.221.132.81/analytics.rcv
    //https://d.btttag.com/analytics.rcv
    private static final String TRACKER_URL = "https://d.btttag.com/analytics.rcv";

    /**
     * The hardcoded browser
     */
    private static final String BROWSER = "Native App";

    /**
     * Singleton instance of the tracker
     */
    private static Tracker instance;

    /**
     * Weak reference to Android application context
     */
    private final WeakReference<Context> context;

    /**
     * A map of fields that should be applied to all timers such as
     */
    public final Map<String, String> globalFields;

    /**
     * URL to submit timers to
     */
    private final String trackerUrl;

    /**
     * The Users BTT site prefix
     */
    public static  String sitePrefix;

    /**
     * The Users BTT site session
     */
    public static  String siteSession;

    /**
     * The Users BTT application name
     */
    public static  String applicationName;


    /**
     * Executor service to queue and submit timers
     */
    private final TrackerExecutor trackerExecutor;

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
    synchronized public static Tracker init(@NonNull final Context context, @Nullable final String siteId, @Nullable final String trackerUrl) {
        if (instance != null) {
            return instance;
        }

        String siteIdentifier = siteId;

        sitePrefix = siteId;
        if (TextUtils.isEmpty(siteIdentifier)) {
            siteIdentifier = Utils.getResourceString(context, SITE_ID_RESOURCE_KEY);
        }

        instance = new Tracker(context, siteIdentifier, TRACKER_URL);
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

    private Tracker(@NonNull final Context context, @NonNull final String siteId, @NonNull final String trackerUrl) {
        super();
        this.context = new WeakReference<>(context);
        this.trackerUrl = trackerUrl;
        globalFields = new HashMap<>(8);
        globalFields.put(Timer.FIELD_SITE_ID, siteId);
        sitePrefix = siteId;
        globalFields.put(Timer.FIELD_BROWSER, BROWSER);
        final String os = String.format("Android %s", Build.VERSION.RELEASE);
        final String appVersion = Utils.getAppVersion(context);
        globalFields.put(Timer.FIELD_NATIVE_OS, os);
        final boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        globalFields.put(Timer.FIELD_DEVICE, isTablet ? "Tablet" : "Mobile");
        globalFields.put(Timer.FIELD_BROWSER_VERSION, String.format("%s-%s-%s", BROWSER, appVersion, os));
        this.applicationName =getApplicationName(context);

        final String sessionId = Utils.generateRandomId();
        final String globalUserId = getOrCreateGlobalUserId();
        siteSession = sessionId;
        setSessionId(sessionId);
        setGlobalUserId(globalUserId);

        this.trackerExecutor = new TrackerExecutor();
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
        appName = appName +"%20"+ String.format("Android%s", Build.VERSION.RELEASE);

        return appName.replaceAll(" ", "%20");
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

        final SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Timer.FIELD_GLOBAL_USER_ID)) {
            globalUserId = sharedPreferences.getString(Timer.FIELD_GLOBAL_USER_ID, null);
        }

        if (TextUtils.isEmpty(globalUserId)) {
            globalUserId = Utils.generateRandomId();
            sharedPreferences.edit().putString(Timer.FIELD_GLOBAL_USER_ID, globalUserId).apply();
        }

        return globalUserId;
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

        trackerExecutor.submit(new TrackerExecutor.TimerRunnable(this.trackerUrl, timer));
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

    public void trackCrashes(){
        //http://3.221.132.81/err.rcv
        //https://d.btttag.com/err.rcv
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof BtCrashHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new BtCrashHandler(
                    "https://d.btttag.com/err.rcv", sitePrefix, siteSession,this.trackerUrl,applicationName));
        }
    }

    public void raiseTestException() {
        int a = 10, b = 0;
        System.out.println(a/b);
    }

}
