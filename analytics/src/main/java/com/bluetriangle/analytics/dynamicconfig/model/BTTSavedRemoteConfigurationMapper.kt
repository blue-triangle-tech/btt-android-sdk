/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_ANR_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_CRASH_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_GROUPING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_LAUNCH_TIME
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_MEMORY_WARNING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_NETWORK_STATE_TRACKING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_WEB_VIEW_STITCHING
import com.bluetriangle.analytics.Constants.DEFAULT_ENABLE_GROUPING_TAP_DETECTION
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import org.json.JSONArray
import org.json.JSONObject

internal object BTTSavedRemoteConfigurationMapper {
    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"
    private const val SAVED_DATE = "savedDate"
    private const val IGNORE_SCREENS = "ignoreScreens"
    private const val ENABLE_ALL_TRACKING = "enableAllTracking"
    private const val ENABLE_GROUPING = "enableGrouping"
    private const val GROUPING_IDLE_TIME = "groupingIdleTime"
    private const val ENABLE_SCREEN_TRACKING = "enableScreenTracking"
    private const val ENABLE_GROUPING_TAP_DETECTION = "enableGroupingTapDetection"
    private const val ENABLE_NETWORK_STATE_TRACKING = "enableNetworkStateTracking"
    private const val ENABLE_CRASH_TRACKING = "enableCrashTracking"
    private const val ENABLE_ANR_TRACKING = "enableANRTracking"
    private const val ENABLE_MEMORY_WARNING = "enableMemoryWarning"
    private const val ENABLE_LAUNCH_TIME = "enableLaunchTime"
    private const val ENABLE_WEB_VIEW_STITCHING = "enableWebViewStitching"

    fun fromJson(jsonObject: JSONObject, defaultConfig: BTTRemoteConfiguration): BTTSavedRemoteConfiguration {
        val ignoreScreens = jsonObject.getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
            buildList {
                for (i in 0 until array.length()) {
                    array.optString(i, null)?.also { screen ->
                        add(screen)
                    }
                }
            }
        } ?: listOf()

        return BTTSavedRemoteConfiguration(
            jsonObject.getDoubleOrNull(NETWORK_SAMPLE_RATE),
            ignoreScreens,
            jsonObject.getBoolean(ENABLE_REMOTE_CONFIG),
            jsonObject.getBooleanOrNull(ENABLE_ALL_TRACKING)?:defaultConfig.enableAllTracking,
            jsonObject.getBooleanOrNull(ENABLE_SCREEN_TRACKING)?:defaultConfig.enableScreenTracking,
            jsonObject.getBooleanOrNull(ENABLE_GROUPING)?:DEFAULT_ENABLE_GROUPING,
            jsonObject.getIntOrNull(GROUPING_IDLE_TIME)?: Constants.DEFAULT_GROUPING_IDLE_TIME,
            jsonObject.getBooleanOrNull(ENABLE_GROUPING_TAP_DETECTION) ?: DEFAULT_ENABLE_GROUPING_TAP_DETECTION,
            jsonObject.getBooleanOrNull(ENABLE_NETWORK_STATE_TRACKING) ?: DEFAULT_ENABLE_NETWORK_STATE_TRACKING,
            jsonObject.getBooleanOrNull(ENABLE_CRASH_TRACKING) ?: DEFAULT_ENABLE_CRASH_TRACKING,
            jsonObject.getBooleanOrNull(ENABLE_ANR_TRACKING) ?: DEFAULT_ENABLE_ANR_TRACKING,
            jsonObject.getBooleanOrNull(ENABLE_MEMORY_WARNING) ?: DEFAULT_ENABLE_MEMORY_WARNING,
            jsonObject.getBooleanOrNull(ENABLE_LAUNCH_TIME) ?: DEFAULT_ENABLE_LAUNCH_TIME,
            jsonObject.getBooleanOrNull(ENABLE_WEB_VIEW_STITCHING) ?: DEFAULT_ENABLE_WEB_VIEW_STITCHING,
            jsonObject.getLong(SAVED_DATE)
        )
    }

    fun toJSONObject(config: BTTSavedRemoteConfiguration) = JSONObject().apply {
        val ignoreListArray = JSONArray()
        config.ignoreScreens.forEach {
            ignoreListArray.put(it)
        }
        put(NETWORK_SAMPLE_RATE, config.networkSampleRate)
        put(IGNORE_SCREENS, ignoreListArray)
        put(ENABLE_ALL_TRACKING, config.enableAllTracking)
        put(ENABLE_REMOTE_CONFIG, config.enableRemoteConfigAck)
        put(ENABLE_SCREEN_TRACKING, config.enableScreenTracking)
        put(ENABLE_GROUPING, config.enableGrouping)
        put(GROUPING_IDLE_TIME, config.groupingIdleTime)
        put(ENABLE_GROUPING_TAP_DETECTION, config.enableGroupingTapDetection)
        put(ENABLE_NETWORK_STATE_TRACKING, config.enableNetworkStateTracking)
        put(ENABLE_CRASH_TRACKING, config.enableCrashTracking)
        put(ENABLE_ANR_TRACKING, config.enableANRTracking)
        put(ENABLE_MEMORY_WARNING, config.enableMemoryWarning)
        put(ENABLE_LAUNCH_TIME, config.enableLaunchTime)
        put(ENABLE_WEB_VIEW_STITCHING, config.enableWebViewStitching)
        put(SAVED_DATE, config.savedDate)
    }

}