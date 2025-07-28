/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import org.json.JSONObject

internal object BTTRemoteConfigurationMapper {

    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"
    private const val IGNORE_SCREENS = "ignoreScreens"
    private const val ENABLE_ALL_TRACKING = "enableAllTracking"
    private const val ENABLE_GROUPING = "enableGrouping"
    private const val GROUPING_IDLE_TIME = "groupingIdleTime"
    private const val ENABLE_SCREEN_TRACKING = "enableScreenTracking"
    private const val GROUPED_VIEW_SAMPLE_RATE = "groupedViewSampleRate"

    fun fromJson(remoteConfigJson: JSONObject): BTTRemoteConfiguration {
        val networkSampleRate = remoteConfigJson.getIntOrNull(NETWORK_SAMPLE_RATE)?.div(100.0)
        val enableRemoteConfig = remoteConfigJson.getBooleanOrNull(ENABLE_REMOTE_CONFIG) == true
        val ignoreScreens = remoteConfigJson.getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
            buildList {
                for (i in 0 until array.length()) {
                    array.optString(i, null)?.also { screen ->
                        add(screen.trim())
                    }
                }
            }
        } ?: listOf()
        val enableAllTracking = remoteConfigJson.getBooleanOrNull(ENABLE_ALL_TRACKING) != false
        val enableScreenTracking = remoteConfigJson.getBooleanOrNull(ENABLE_SCREEN_TRACKING) != false
        val enableGrouping = remoteConfigJson.getBooleanOrNull(ENABLE_GROUPING) == true
        val groupingIdleTime = remoteConfigJson.getIntOrNull(GROUPING_IDLE_TIME) ?: Constants.DEFAULT_GROUPING_IDLE_TIME
        val groupedViewSampleRate = remoteConfigJson.getDoubleOrNull(GROUPED_VIEW_SAMPLE_RATE)

        return BTTRemoteConfiguration(
            networkSampleRate,
            ignoreScreens,
            enableAllTracking,
            enableRemoteConfig,
            enableScreenTracking,
            enableGrouping,
            groupingIdleTime,
            groupedViewSampleRate
        )
    }
}