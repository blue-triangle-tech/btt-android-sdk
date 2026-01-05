/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_ANR_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_CRASH_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_GROUPED_VIEW_SAMPLE_RATE
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_GROUPING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_GROUPING_TAP_DETECTION
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_LAUNCH_TIME
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_MEMORY_WARNING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_NETWORK_STATE_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_WEB_VIEW_STITCHING
import com.bluetriangle.analytics.Constants.DEFAULT_NETWORK_SAMPLE_RATE
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import com.bluetriangle.analytics.utility.getStringOrNull
import org.json.JSONArray
import org.json.JSONObject

internal data class SessionData(
    val sessionId: String,
    val shouldSampleNetwork: Boolean,
    val isConfigApplied: Boolean,
    val networkSampleRate: Double,
    val ignoreScreens: List<String>,
    val enableScreenTracking: Boolean,
    val enableGrouping: Boolean,
    val groupingIdleTime: Int,
    val groupedViewSampleRate: Double,
    val shouldSampleGroupedView: Boolean,
    val enableGroupingTapDetection: Boolean,
    val enableNetworkStateTracking: Boolean,
    val enableCrashTracking: Boolean,
    val enableANRTracking: Boolean,
    val enableMemoryWarning: Boolean,
    val enableLaunchTime: Boolean,
    val enableWebViewStitching: Boolean,
    val expiration: Long
) {
    companion object {
        private const val SESSION_ID = "sessionId"
        private const val EXPIRATION = "expiration"
        private const val SHOULD_SAMPLE_NETWORK = "shouldSampleNetwork"
        private const val IS_CONFIG_APPLIED = "isConfigApplied"
        private const val NETWORK_SAMPLE_RATE = "networkSampleRate"
        private const val IGNORE_SCREENS = "ignoreScreens"
        private const val ENABLE_SCREEN_TRACKING = "enableScreenTracking"
        private const val ENABLE_GROUPING = "enableGrouping"
        private const val GROUPING_IDLE_TIME = "groupingIdleTime"
        private const val GROUPED_VIEW_SAMPLE_RATE = "groupedViewSampleRate"
        private const val SHOULD_SAMPLE_GROUPED_VIEW = "shouldSampleGroupedView"
        private const val ENABLE_GROUPING_TAP_DETECTION = "enableGroupingTapDetection"
        private const val ENABLE_NETWORK_STATE_TRACKING = "enableNetworkStateTracking"
        private const val ENABLE_CRASH_TRACKING = "enableCrashTracking"
        private const val ENABLE_ANR_TRACKING = "enableANRTracking"
        private const val ENABLE_MEMORY_WARNING = "enableMemoryWarning"
        private const val ENABLE_LAUNCH_TIME = "enableLaunchTime"
        private const val ENABLE_WEB_VIEW_STITCHING = "enableWebViewStitching"

        internal fun JSONObject.toSessionData(): SessionData? {
            try {
                return SessionData(
                    sessionId = getStringOrNull(SESSION_ID)?:return null,
                    shouldSampleNetwork = getBooleanOrNull(SHOULD_SAMPLE_NETWORK)?:false,
                    isConfigApplied = getBooleanOrNull(IS_CONFIG_APPLIED)?:false,
                    networkSampleRate = getDoubleOrNull(NETWORK_SAMPLE_RATE)?:DEFAULT_NETWORK_SAMPLE_RATE,
                    ignoreScreens = getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
                        buildList {
                            repeat(array.length()) {
                                add(array.getString(it))
                            }
                        }
                    } ?: listOf(),
                    enableScreenTracking = getBooleanOrNull(ENABLE_SCREEN_TRACKING) != false,
                    enableGrouping = getBooleanOrNull(ENABLE_GROUPING)?:DEFAULT_ENABLE_GROUPING,
                    groupingIdleTime = getIntOrNull(GROUPING_IDLE_TIME) ?: Constants.DEFAULT_GROUPING_IDLE_TIME,
                    groupedViewSampleRate = getDoubleOrNull(GROUPED_VIEW_SAMPLE_RATE)?:DEFAULT_GROUPED_VIEW_SAMPLE_RATE,
                    shouldSampleGroupedView = getBooleanOrNull(SHOULD_SAMPLE_GROUPED_VIEW) == true,
                    enableGroupingTapDetection = getBooleanOrNull(ENABLE_GROUPING_TAP_DETECTION) ?: DEFAULT_ENABLE_GROUPING_TAP_DETECTION,
                    enableNetworkStateTracking = getBooleanOrNull(ENABLE_NETWORK_STATE_TRACKING) ?: DEFAULT_ENABLE_NETWORK_STATE_TRACKING,
                    enableCrashTracking = getBooleanOrNull(ENABLE_CRASH_TRACKING) ?: DEFAULT_ENABLE_CRASH_TRACKING,
                    enableANRTracking = getBooleanOrNull(ENABLE_ANR_TRACKING) ?: DEFAULT_ENABLE_ANR_TRACKING,
                    enableMemoryWarning = getBooleanOrNull(ENABLE_MEMORY_WARNING) ?: DEFAULT_ENABLE_MEMORY_WARNING,
                    enableLaunchTime = getBooleanOrNull(ENABLE_LAUNCH_TIME) ?: DEFAULT_ENABLE_LAUNCH_TIME,
                    enableWebViewStitching = getBooleanOrNull(ENABLE_WEB_VIEW_STITCHING) ?: DEFAULT_ENABLE_WEB_VIEW_STITCHING,
                    expiration = getLong(EXPIRATION)
                )
            } catch (e: Exception) {
                Tracker.instance?.configuration?.logger?.error("Error while parsing session data: ${e::class.simpleName}(\"${e.message}\")")
                return null
            }
        }

        internal fun SessionData.toJsonObject() = JSONObject().apply {
            put(SESSION_ID, sessionId)
            put(SHOULD_SAMPLE_NETWORK, shouldSampleNetwork)
            put(IS_CONFIG_APPLIED, isConfigApplied)
            put(NETWORK_SAMPLE_RATE, networkSampleRate)
            put(IGNORE_SCREENS, JSONArray(ignoreScreens))
            put(ENABLE_SCREEN_TRACKING, enableScreenTracking)
            put(ENABLE_GROUPING, enableGrouping)
            put(GROUPING_IDLE_TIME, groupingIdleTime)
            put(GROUPED_VIEW_SAMPLE_RATE, groupedViewSampleRate)
            put(SHOULD_SAMPLE_GROUPED_VIEW, shouldSampleGroupedView)
            put(ENABLE_GROUPING_TAP_DETECTION, enableGroupingTapDetection)
            put(ENABLE_NETWORK_STATE_TRACKING, enableNetworkStateTracking)
            put(ENABLE_CRASH_TRACKING, enableCrashTracking)
            put(ENABLE_ANR_TRACKING, enableANRTracking)
            put(ENABLE_MEMORY_WARNING, enableMemoryWarning)
            put(ENABLE_LAUNCH_TIME, enableLaunchTime)
            put(ENABLE_WEB_VIEW_STITCHING, enableWebViewStitching)
            put(EXPIRATION, expiration)
        }
    }
}