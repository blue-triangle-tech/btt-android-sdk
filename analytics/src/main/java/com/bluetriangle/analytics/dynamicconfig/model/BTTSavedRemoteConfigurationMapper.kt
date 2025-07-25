/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getDoubleOrNull
import com.bluetriangle.analytics.utility.getJsonArrayOrNull
import org.json.JSONArray
import org.json.JSONObject

internal object BTTSavedRemoteConfigurationMapper {
    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"
    private const val SAVED_DATE = "savedDate"
    private const val IGNORE_SCREENS = "ignoreScreens"
    private const val ENABLE_ALL_TRACKING = "enableAllTracking"
    private const val ENABLE_SCREEN_TRACKING = "enableScreenTracking"

    fun fromJson(jsonObject: JSONObject): BTTSavedRemoteConfiguration {
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
            jsonObject.getBooleanOrNull(ENABLE_ALL_TRACKING)?: true,
            jsonObject.getBooleanOrNull(ENABLE_SCREEN_TRACKING) != false,
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
        put(SAVED_DATE, config.savedDate)
    }
}