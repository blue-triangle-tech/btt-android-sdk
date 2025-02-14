/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import com.bluetriangle.analytics.utility.getStringOrNull
import org.json.JSONObject

internal object BTTRemoteConfigurationMapper {

    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"
    private const val IGNORE_SCREENS = "ignoreScreens"
    private const val ENABLE_ALL_TRACKING = "enableAllTracking"
    private const val CLARITY_PROJECT_ID = "clarityProjectID"

    fun fromJson(remoteConfigJson: JSONObject): BTTRemoteConfiguration {
        val networkSampleRate = remoteConfigJson.getIntOrNull(NETWORK_SAMPLE_RATE)?.div(100.0)
        val enableRemoteConfig = remoteConfigJson.getBooleanOrNull(ENABLE_REMOTE_CONFIG) ?: false
        val ignoreScreens = remoteConfigJson.getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
            buildList {
                for (i in 0 until array.length()) {
                    array.optString(i, null)?.also { screen ->
                        add(screen.trim())
                    }
                }
            }
        } ?: listOf()
        val enableAllTracking = remoteConfigJson.getBooleanOrNull(ENABLE_ALL_TRACKING) ?: true
        val clarityProjectID = remoteConfigJson.getStringOrNull(CLARITY_PROJECT_ID)

        return BTTRemoteConfiguration(
            networkSampleRate,
            ignoreScreens,
            enableAllTracking,
            enableRemoteConfig,
            clarityProjectID
        )
    }
}