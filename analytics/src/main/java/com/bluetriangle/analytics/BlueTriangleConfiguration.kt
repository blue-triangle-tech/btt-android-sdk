package com.bluetriangle.analytics

import android.util.Log
import java.util.concurrent.TimeUnit

class BlueTriangleConfiguration {
    /**
     * The Blue Triangle site ID
     */
    var siteId: String? = null

    /**
     * Global User ID
     */
    var globalUserId: String? = null

    /**
     * Session ID
     */
    var sessionId: String? = null

    /**
     * the application's name
     */
    var applicationName: String? = null

    /**
     * SDK's user agent
     */
    var userAgent: String? = null

    /**
     * Percentage of sessions for which network calls will be captured. A value of `0.05` means that 5% of session's network requests will be tracked.
     * A value of `0.0` means that no network requests will be captured for the session, a value of `1.0` will track all network requests for a session.
     */
    var networkSampleRate = 0.05
        set(networkSampleRate) {
            field = Math.min(Math.max(networkSampleRate, 0.0), 1.0)
            shouldSampleNetwork = Utils.shouldSample(this.networkSampleRate)
        }

    /**
     * if network requests should be captured
     */
    var shouldSampleNetwork = false

    var isDebug = false
    var debugLevel = Log.DEBUG
    var logger: Logger? = null
        get() {
            if (field == null) {
                field = AndroidLogger(debugLevel)
            }
            return field
        }

    var trackerUrl = DEFAULT_TRACKER_URL

    var errorReportingUrl = DEFAULT_ERROR_REPORTING_URL

    var networkCaptureUrl = DEFAULT_NETWORK_CAPTURE_URL

    /**
     * Cache directory path
     */
    var cacheDirectory: String? = null

    /**
     * Max items in the cache
     */
    var maxCacheItems = 100

    /**
     * Max attempts to resend a payload
     */
    var maxAttempts = 3

    /**
     * the instance to cache payloads
     */
    var payloadCache: PayloadCache? = null
        get() {
            if (field == null) {
                field = PayloadCache(this)
            }
            return field
        }

    var isTrackCrashesEnabled = false

    var isPerformanceMonitorEnabled = false
    var performanceMonitorIntervalMs = TimeUnit.SECONDS.toMillis(1)

    /**
     * Enable or disable ANR detection and sending reports to the server.
     */
    var isTrackAnrEnabled: Boolean = false

    /**
     * time interval for ANR warning based on track ANR is enabled or disabled, default to 5 seconds, minimum is 3 second, if set less then minimum allowed set value is ignored
     */
    var trackAnrIntervalSec = Constants.ANR_DEFAULT_INTERVAL

    var isScreenTrackingEnabled: Boolean = false
    var isLaunchTimeEnabled: Boolean = false

    companion object {
        const val DEFAULT_TRACKER_URL = "https://d.btttag.com/analytics.rcv"
        const val DEFAULT_ERROR_REPORTING_URL = "https://d.btttag.com/err.rcv"
        const val DEFAULT_NETWORK_CAPTURE_URL = "https://d.btttag.com/wcdv02.rcv"
    }
}