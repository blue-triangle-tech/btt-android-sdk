/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.bluetriangle.analytics.Timer.Companion.FIELD_SESSION_ID
import com.bluetriangle.analytics.anrwatchdog.AnrManager
import com.bluetriangle.analytics.deviceinfo.DeviceInfoProvider
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider
import com.bluetriangle.analytics.dynamicconfig.fetcher.BTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigUpdateReporter
import com.bluetriangle.analytics.dynamicconfig.repository.BTTConfigurationRepository
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.dynamicconfig.updater.BTTConfigurationUpdater
import com.bluetriangle.analytics.hybrid.BTTWebViewTracker
import com.bluetriangle.analytics.launchtime.LaunchMonitor
import com.bluetriangle.analytics.launchtime.LaunchReporter
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequestCollection
import com.bluetriangle.analytics.networkstate.NetworkStateMonitor
import com.bluetriangle.analytics.networkstate.NetworkTimelineTracker
import com.bluetriangle.analytics.screenTracking.ActivityLifecycleTracker
import com.bluetriangle.analytics.screenTracking.BTTScreenLifecycleTracker
import com.bluetriangle.analytics.screenTracking.FragmentLifecycleTracker
import com.bluetriangle.analytics.sessionmanager.SessionData
import com.bluetriangle.analytics.sessionmanager.SessionManager
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

/**
 * The tracker is a global object responsible for taking submitted timers and reporting them to the cloud server via a
 * background thread.
 */
class Tracker private constructor(
    application: Application,
    configuration: BlueTriangleConfiguration
) {
    private var anrManager: AnrManager

    /**
     * Weak reference to Android application context
     */
    private val context: WeakReference<Context>

    /**
     * A weak reference to the most recently started timer
     */
    private var mostRecentTimer: WeakReference<Timer>? = null

    /**
     * The tracker's configuration
     */
    val configuration: BlueTriangleConfiguration

    /**
     * A map of fields that should be applied to all timers such as
     */
    val globalFields: MutableMap<String, String>

    /**
     * A map of extended custom variables
     */
    private val customVariables: MutableMap<String, String>

    /**
     * Executor service to queue and submit timers
     */
    private val trackerExecutor: TrackerExecutor

    /**
     * Performance monitoring threads
     */
    private val performanceMonitors = HashMap<Long, PerformanceMonitor>()

    /**
     * Captured requests awaiting to be bulk sent to Blue Triangle API
     */
    private val capturedRequests = ConcurrentHashMap<Long, CapturedRequestCollection>()

    internal val screenTrackMonitor: BTTScreenLifecycleTracker
    private val activityLifecycleTracker: ActivityLifecycleTracker

    internal var networkTimelineTracker: NetworkTimelineTracker? = null
    internal var networkStateMonitor: NetworkStateMonitor? = null
    private var sessionManager: SessionManager
    private val configurationRepository: IBTTConfigurationRepository
    private var deviceInfoProvider: IDeviceInfoProvider

    init {
        this.context = WeakReference(application.applicationContext)
        this.deviceInfoProvider = DeviceInfoProvider()

        val defaultConfig = BTTSavedRemoteConfiguration(configuration.networkSampleRate, listOf(), false, 0L)
        this.configurationRepository = BTTConfigurationRepository(
            application.applicationContext,
            configuration.siteId?:"",
            defaultConfig = defaultConfig
        )
        this.configuration = configuration

        val configUrl = "https://d.btttag.com/config.php?siteID=${configuration.siteId}"
        val configUpdater = BTTConfigurationUpdater(
            repository = this.configurationRepository,
            fetcher = BTTConfigurationFetcher(configUrl),
            60 * 60 * 1000,
            reporter = BTTConfigUpdateReporter(
                this.configuration,
                this.deviceInfoProvider
            )
        )
        this.sessionManager = SessionManager(
            application.applicationContext,
            this.configuration.siteId?:"",
            this.configuration.sessionExpiryDuration,
            this.configurationRepository,
            configUpdater,
            defaultConfig
        )
        AppEventHub.instance.addConsumer(this.sessionManager)

        globalFields = HashMap(8)
        customVariables = mutableMapOf()
        configuration.siteId?.let { globalFields[Timer.FIELD_SITE_ID] = it }
        globalFields[Timer.FIELD_BROWSER] = Constants.BROWSER
        globalFields[Timer.FIELD_NA_FLG] = "1"
        val appVersion = Utils.getAppVersion(application.applicationContext)
        val isTablet = Utils.isTablet(application.applicationContext)
        globalFields[Timer.FIELD_DEVICE] =
            if (isTablet) Constants.DEVICE_TABLET else Constants.DEVICE_MOBILE
        globalFields[Timer.FIELD_BROWSER_VERSION] = "${Constants.BROWSER}-$appVersion-${Utils.os}"
        globalFields[Timer.FIELD_SDK_VERSION] = BuildConfig.SDK_VERSION

        setGlobalUserId(globalUserId)
        configuration.globalUserId = globalUserId

        val sessionData = sessionManager.sessionData
        setSessionId(sessionData.sessionId)
        this.configuration.sessionId = sessionData.sessionId
        this.configuration.shouldSampleNetwork = sessionData.shouldSampleNetwork

        trackerExecutor = TrackerExecutor(configuration)
        screenTrackMonitor = BTTScreenLifecycleTracker(
            configuration.isScreenTrackingEnabled,
            sessionData.ignoreScreens
        )

        val fragmentLifecycleTracker = FragmentLifecycleTracker(screenTrackMonitor)
        activityLifecycleTracker = ActivityLifecycleTracker(
            screenTrackMonitor,
            fragmentLifecycleTracker
        )
        application.registerActivityLifecycleCallbacks(activityLifecycleTracker)

        anrManager = AnrManager(configuration, deviceInfoProvider)

        if (configuration.isTrackAnrEnabled) {
            anrManager.start()
        }
        if (configuration.isTrackCrashesEnabled) {
            trackCrashes()
        }

        initializeNetworkMonitoring()
        if (configuration.isLaunchTimeEnabled) {
            logLaunchMonitorErrors()
            LaunchReporter(configuration.logger, LaunchMonitor.instance)
        }
        configuration.logger?.debug("BlueTriangleSDK Initialized: $configuration")
    }

    private fun logLaunchMonitorErrors() {
        val logs = LaunchMonitor.instance.logs
        for (log in logs) {
            configuration.logger?.log(log.level, log.message)
        }
    }

    private fun initializeNetworkMonitoring() {
        if (!configuration.isTrackNetworkStateEnabled) return

        val appContext = context.get()

        if (appContext == null) {
            configuration.logger?.error("Unable to start network monitoring: Context is null")
            return
        }

        val hasNetworkStatePermission = ContextCompat.checkSelfPermission(
            appContext,
            ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasNetworkStatePermission) {
            configuration.logger?.error("Unable to start network monitoring: Missing permission (ACCESS_NETWORK_STATE)")
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            configuration.logger?.error("Unable to start network monitoring: Unsupported Android version.")
            return
        }

        networkStateMonitor = NetworkStateMonitor(configuration.logger, appContext)
        networkTimelineTracker = NetworkTimelineTracker(networkStateMonitor!!)

        configuration.logger?.debug("Network state monitoring started.")
    }

    @Synchronized
    fun setMostRecentTimer(timer: Timer) {
        mostRecentTimer = WeakReference(timer)
    }

    fun getMostRecentTimer(): Timer? {
        return if (mostRecentTimer != null) {
            mostRecentTimer!!.get()
        } else null
    }

    /**
     * Get the stored global user ID or generate and persist if not found
     *
     * @return the global user ID
     */
    private val globalUserId: String
        get() {
            var globalUserId: String? = null
            val context = context.get() ?: return Utils.generateRandomId()
            val sharedPreferences =
                context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            if (sharedPreferences.contains(Timer.FIELD_GLOBAL_USER_ID)) {
                globalUserId = sharedPreferences.getString(Timer.FIELD_GLOBAL_USER_ID, null)
            }
            if (globalUserId.isNullOrBlank()) {
                globalUserId = Utils.generateRandomId()
                sharedPreferences.edit().putString(Timer.FIELD_GLOBAL_USER_ID, globalUserId).apply()
            }
            return globalUserId
        }

    fun createPerformanceMonitor(): PerformanceMonitor {
        val performanceMonitor = PerformanceMonitor(configuration, deviceInfoProvider)
        performanceMonitors[performanceMonitor.id] = performanceMonitor
        return performanceMonitor
    }

    fun getPerformanceMonitor(id: Long): PerformanceMonitor? {
        return performanceMonitors[id]
    }

    fun clearPerformanceMonitor(id: Long) {
        performanceMonitors.remove(id)
    }

    /**
     * Submit a timer to the tracker to be sent to the cloud server.
     *
     *
     * If the timer has not been ended, it will be ended on submit.
     *
     * @param timer The timer to submit
     */
    fun submitTimer(timer: Timer) {
        if (!timer.hasEnded()) {
            timer.end()
        }
        timer.setFields(globalFields.toMap())
        if (customVariables.isNotEmpty()) {
            kotlin.runCatching {
                val extendedCustomVariables = JSONObject(customVariables as Map<*, *>?).toString()
                if (extendedCustomVariables.length > Constants.EXTENDED_CUSTOM_VARIABLE_MAX_PAYLOAD) {
                    configuration.logger?.warn("Dropping extended custom variables for $timer. Payload ${extendedCustomVariables.length} exceeds max size of ${Constants.EXTENDED_CUSTOM_VARIABLE_MAX_PAYLOAD}")
                } else {
                    timer.setField(Timer.FIELD_EXTENDED_CUSTOM_VARIABLES, extendedCustomVariables)
                }
            }
        }
        timer.nativeAppProperties.add(deviceInfoProvider.getDeviceInfo())
        timer.setField(FIELD_SESSION_ID, sessionManager.sessionData.sessionId)
        trackerExecutor.submit(TimerRunnable(configuration, timer))
    }

    /**
     * Submit a captured network request to the tracker to send to
     *
     * @param capturedRequest
     */
    @Synchronized
    fun submitCapturedRequest(capturedRequest: CapturedRequest?) {
        if (capturedRequest == null) return
        if (configuration.shouldSampleNetwork) {
            getMostRecentTimer()?.let { timer ->
                configuration.logger?.debug("Network Request Captured: $capturedRequest for $timer")
                capturedRequest.setNavigationStart(timer.start)
                if (capturedRequests.containsKey(timer.start)) {
                    capturedRequests[timer.start]!!.add(capturedRequest)
                } else {
                    val capturedRequestCollection = CapturedRequestCollection(
                        configuration.siteId.toString(),
                        timer.start.toString(),
                        getTimerValue(Timer.FIELD_PAGE_NAME, timer),
                        getTimerValue(Timer.FIELD_CONTENT_GROUP_NAME, timer),
                        getTimerValue(Timer.FIELD_TRAFFIC_SEGMENT_NAME, timer),
                        configuration.sessionId.toString(),
                        globalFields[Timer.FIELD_BROWSER_VERSION]!!,
                        globalFields[Timer.FIELD_DEVICE]!!,
                        deviceInfoProvider = deviceInfoProvider,
                        capturedRequest
                    )
                    capturedRequests[timer.start] = capturedRequestCollection
                }
            }
        }
    }

    /**
     * Returns a list of captured request collections for the current timer as well as all past timers to send
     */
    @Synchronized
    fun getCapturedRequestCollectionsForTimer(timer: Timer): List<CapturedRequestCollection> {
        val keysToSend = capturedRequests.keys().toList().filter { it <= timer.start }
        val capturedRequestCollections = mutableListOf<CapturedRequestCollection>()
        keysToSend.forEach {
            capturedRequests.remove(it)
                ?.let { collection -> capturedRequestCollections.add(collection) }
        }
        return capturedRequestCollections.toList()
    }

    private fun getTimerValue(fieldName: String, timer: Timer?): String {
        if (timer != null) {
            val value = timer.getField(fieldName)
            if (value != null && !TextUtils.isEmpty(value)) {
                return value
            }
        }
        val value = globalFields[fieldName]
        return if (value != null && !TextUtils.isEmpty(value)) {
            value
        } else ""
    }

    /**
     * Submit a cached payload to the executor
     *
     * @param payload cached payload to retry sending
     */
    fun submitPayload(payload: Payload) {
        trackerExecutor.submit(PayloadRunnable(configuration, payload))
    }

    /**
     * set the current session ID for this tracker
     *
     * @param sessionId session ID to send with all timers submitted
     */
    fun setSessionId(sessionId: String) {
        setGlobalField(Timer.FIELD_SESSION_ID, sessionId)
    }

    /**
     * set the global user ID for this tracker
     *
     * @param globalUserId the global user ID to send with all timers submitted
     */
    fun setGlobalUserId(globalUserId: String) {
        setGlobalField(Timer.FIELD_GLOBAL_USER_ID, globalUserId)
    }

    /**
     * Set this session's AB test identifier
     *
     * @param abTestIdentifier the AB test id
     */
    fun setSessionAbTestIdentifier(abTestIdentifier: String) {
        setGlobalField(Timer.FIELD_AB_TEST_ID, abTestIdentifier)
    }

    /**
     * Set this session's data center value
     *
     * @param dataCenter the value for the data center
     */
    fun setSessionDataCenter(dataCenter: String) {
        setGlobalField(Timer.FIELD_DATACENTER, dataCenter)
    }

    /**
     * Set this session's traffic segment name
     *
     * @param trafficSegmentName name of the traffic segment for this session
     */
    fun setSessionTrafficSegmentName(trafficSegmentName: String) {
        setGlobalField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, trafficSegmentName)
    }

    /**
     * Set this session's campaign name
     *
     * @param campaignName name of campaign
     */
    fun setSessionCampaignName(campaignName: String) {
        setGlobalField(Timer.FIELD_CAMPAIGN_NAME, campaignName)
    }

    /**
     * Set this session's campaign source
     *
     * @param campaignSource source of campaign
     */
    fun setSessionCampaignSource(campaignSource: String) {
        setGlobalField(Timer.FIELD_CAMPAIGN_SOURCE, campaignSource)
    }

    /**
     * Set this session's campaign medium
     *
     * @param campaignMedium medium of campaign
     */
    fun setSessionCampaignMedium(campaignMedium: String) {
        setGlobalField(Timer.FIELD_CAMPAIGN_MEDIUM, campaignMedium)
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: String) {
        synchronized(globalFields) { globalFields.put(fieldName, value) }
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: Int) {
        setGlobalField(fieldName, value.toString())
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: Double) {
        setGlobalField(fieldName, value.toString())
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: Float) {
        setGlobalField(fieldName, value.toString())
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: Boolean) {
        setGlobalField(fieldName, value.toString())
    }

    /**
     * Set a global field to be applied to all trackers
     *
     * @param fieldName the name of the field
     * @param value     the value to set for the given field
     */
    fun setGlobalField(fieldName: String, value: Long) {
        setGlobalField(fieldName, value.toString())
    }

    /**
     * Get the current value of a global field
     *
     * @param fieldName the name of the field to get
     * @return the value of the given field name or null if doesn't exist
     */
    fun getGlobalField(fieldName: String): String? {
        return globalFields[fieldName]
    }

    /**
     * Clear a global field from being set on all trackers
     *
     * @param fieldName the name of the field to remove
     */
    fun clearGlobalField(fieldName: String) {
        synchronized(globalFields) { globalFields.remove(fieldName) }
    }

    @Synchronized
    internal fun updateSession(sessionData: SessionData) {
        if (configuration.sessionId == sessionData.sessionId &&
            configuration.shouldSampleNetwork == sessionData.shouldSampleNetwork &&
            configuration.networkSampleRate == sessionData.networkSampleRate &&
            screenTrackMonitor.ignoreScreens.joinToString(",") == sessionData.ignoreScreens.joinToString(",")) return

        configuration.logger?.debug("Updating session Data from ${configuration.sessionId}:${configuration.networkSampleRate}:${configuration.shouldSampleNetwork} to ${sessionData.sessionId}:${sessionData.networkSampleRate}:${sessionData.shouldSampleNetwork}")
        configuration.sessionId = sessionData.sessionId
        configuration.networkSampleRate = sessionData.networkSampleRate
        configuration.shouldSampleNetwork = sessionData.shouldSampleNetwork
        screenTrackMonitor.ignoreScreens = sessionData.ignoreScreens
        setSessionId(sessionData.sessionId)
        BTTWebViewTracker.updateSession(sessionData.sessionId)
    }

    /**
     * Set custom variable for the given name to the given value
     * @param name name of the custom variable to set
     * @param value value of the custom variable to set
     */
    fun setCustomVariable(name: String, value: String) {
        if (value.length > Constants.EXTENDED_CUSTOM_VARIABLE_MAX_LENGTH) {
            configuration.logger?.warn("Extended Custom Variable \"$name\" exceeds max length of ${Constants.EXTENDED_CUSTOM_VARIABLE_MAX_LENGTH}")
        }
        synchronized(customVariables) {
            customVariables.put(name, value)
        }
    }

    /**
     * Set custom variable for the given name to the given value
     * @param name name of the custom variable to set
     * @param value value of the custom variable to set
     */
    fun setCustomVariable(name: String, value: Number) {
        setCustomVariable(name, value.toString())
    }

    /**
     * Set custom variable for the given name to the given value
     * @param name name of the custom variable to set
     * @param value value of the custom variable to set
     */
    fun setCustomVariable(name: String, value: Boolean) {
        setCustomVariable(name, value.toString())
    }

    /**
     * Set all of the variables in the given flat map.  Values must be a String, Number, or Boolean.
     * Does not clear any existing variables.
     */
    fun setCustomVariables(variables: Map<String, String>) {
        for (entry in variables.entries.iterator()) {
            if (entry.value.length > Constants.EXTENDED_CUSTOM_VARIABLE_MAX_LENGTH) {
                configuration.logger?.warn("Extended Custom Variable \"${entry.key}\" exceeds max length of ${Constants.EXTENDED_CUSTOM_VARIABLE_MAX_LENGTH}")
            }
        }
        synchronized(customVariables) {
            customVariables.putAll(variables)
        }
    }

    /**
     * Return the named custom variable, or null
     *
     * @param name of the custom variable to return
     * @return the value of the custom variable or null
     */
    fun getCustomVariable(name: String): String? {
        return kotlin.runCatching { customVariables[name] }.getOrNull()
    }

    /**
     * Get the current custom variables
     * @return a copy of the current custom variables
     */
    fun getCustomVariables(): Map<String, String> {
        return customVariables.toMap()
    }

    /**
     * Clears the given custom variable, if set
     * @param name key of the custom variable to remove
     */
    fun clearCustomVariable(name: String) {
        synchronized(customVariables) {
            customVariables.remove(name)
        }
    }

    /**
     * Removes all custom variables
     */
    fun clearAllCustomVariables() {
        synchronized(customVariables) {
            customVariables.clear()
        }
    }

    fun trackCrashes() {
        if (Thread.getDefaultUncaughtExceptionHandler() !is BtCrashHandler) {
            Thread.setDefaultUncaughtExceptionHandler(
                BtCrashHandler(
                    configuration,
                    deviceInfoProvider
                )
            )
        }
    }

    fun trackError(message: String) {
        trackException(message, object : Throwable() {
            override fun fillInStackTrace(): Throwable {
                stackTrace = arrayOf()
                return this
            }
        })
    }

    /**
     * Manually track an error exception caught by your code
     *
     * @param message   optional message included with the stack trace
     * @param exception the exception to track
     */
    fun trackException(
        message: String?,
        exception: Throwable,
        errorType: BTErrorType = BTErrorType.NativeAppCrash
    ) {
        val timeStamp = System.currentTimeMillis().toString()
        val mostRecentTimer = getMostRecentTimer()
        val crashHitsTimer = Timer().start()
        if (mostRecentTimer != null) {
            mostRecentTimer.generateNativeAppProperties()
            crashHitsTimer.nativeAppProperties = mostRecentTimer.nativeAppProperties
        }
        crashHitsTimer.nativeAppProperties.add(deviceInfoProvider.getDeviceInfo())
        crashHitsTimer.setError(true)

        val stacktrace = Utils.exceptionToStacktrace(message, exception)
        trackerExecutor.submit(
            CrashRunnable(
                configuration,
                stacktrace,
                timeStamp,
                crashHitsTimer,
                errorType,
                mostRecentTimer,
                deviceInfoProvider = deviceInfoProvider
            )
        )
    }

    enum class BTErrorType(val value: String) {
        NativeAppCrash("NativeAppCrash"),
        ANRWarning("ANRWarning"),
        MemoryWarning("MemoryWarning"),
        BTTConfigUpdateError("BTTConfigUpdateError")
    }

    fun raiseTestException() {
        val a = 10
        val b = 0
        println(a / b)
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "BTT_SHARED_PREFERENCES"

        /**
         * String resource name for the site ID
         */
        private const val SITE_ID_RESOURCE_KEY = "btt_site_id"

        /**
         * Gets the singleton tracker instance to submit timers to. If not setup via the builder, it will be null.
         *
         * @return Singleton tracker instance or null if not built yet.
         */
        /**
         * Singleton instance of the tracker
         */
        @JvmStatic
        var instance: Tracker? = null
            private set

        /**
         * Initialize the tracker with default tracker URL and Site ID from string resources.
         *
         * @param application host application instance
         * @return the initialized tracker or null if no site ID
         */
        @JvmStatic
        fun init(application: Application): Tracker? {
            return init(application, BlueTriangleConfiguration())
        }

        /**
         * Initialize the tracker with default tracker URL and given Site ID
         *
         * @param application host application instance
         * @param siteId  Site ID to send with all timers
         * @return the initialized tracker or null if no site ID
         */
        @JvmStatic
        fun init(application: Application, siteId: String?): Tracker? {
            val configuration = BlueTriangleConfiguration()
            configuration.siteId = siteId
            return init(application, configuration)
        }

        /**
         * Initialize the tracker with default tracker URL and given Site ID
         *
         * @param application host application instance
         * @param siteId  Site ID to send with all timers
         * @param trackerUrl the URL to submit timer data
         * @return the initialized tracker or null if no site ID
         */
        @JvmStatic
        fun init(application: Application, siteId: String?, trackerUrl: String?): Tracker? {
            val configuration = BlueTriangleConfiguration()
            configuration.siteId = siteId
            if (!trackerUrl.isNullOrBlank()) {
                configuration.trackerUrl = trackerUrl
            }
            return init(application, configuration)
        }

        /**
         * Initialize the tracker with the given configuration
         *
         * @param application host application instance
         * @param configuration Blue Triangle Configuration
         * @return the initialized tracker or null if no site ID
         */
        @JvmStatic
        @Synchronized
        fun init(application: Application, configuration: BlueTriangleConfiguration): Tracker? {
            if (instance != null) {
                return instance
            }

            if (configuration.isDebug) {
                configuration.logger = AndroidLogger(configuration.debugLevel)
            }

            MetadataReader.applyMetadata(application, configuration)

            if (configuration.applicationName.isNullOrBlank()) {
                configuration.applicationName = Utils.getAppNameAndOs(application)
            }

            if (configuration.userAgent.isNullOrBlank()) {
                configuration.userAgent = Utils.buildUserAgent(application)
            }

            if (configuration.cacheDirectory.isNullOrBlank()) {
                val cacheDir = File(application.cacheDir, "bta")
                if (!cacheDir.exists()) {
                    if (!cacheDir.mkdir()) {
                        configuration.logger?.error("Error creating cache directory: ${cacheDir.absolutePath}")
                    }
                }
                configuration.cacheDirectory = cacheDir.absolutePath
            }

            // if site id is still not configured, try legacy resource string method
            if (configuration.siteId.isNullOrBlank()) {
                val resourceSiteID = Utils.getResourceString(application, SITE_ID_RESOURCE_KEY)
                if (!resourceSiteID.isNullOrBlank()) {
                    configuration.siteId = resourceSiteID
                }
            }

            // if still no site ID, log error
            if (configuration.siteId.isNullOrBlank()) {
                configuration.logger?.error("Site ID is required.")
                return null
            }

            instance = Tracker(application, configuration)
            return instance
        }
    }

}