/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.Constants
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
    val groupingEnabled: Boolean,
    val groupingIdleTime: Int,
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
        private const val GROUPING_ENABLED = "groupingEnabled"
        private const val GROUPING_IDLE_TIME = "groupingIdleTime"

        internal fun JSONObject.toSessionData(): SessionData? {
            try {
                return SessionData(
                    sessionId = getStringOrNull(SESSION_ID)?:return null,
                    shouldSampleNetwork = getBooleanOrNull(SHOULD_SAMPLE_NETWORK)?:false,
                    isConfigApplied = getBooleanOrNull(IS_CONFIG_APPLIED)?:false,
                    networkSampleRate = getDoubleOrNull(NETWORK_SAMPLE_RATE)?:0.0,
                    ignoreScreens = getJsonArrayOrNull(IGNORE_SCREENS)?.let { array ->
                        buildList {
                            repeat(array.length()) {
                                add(array.getString(it))
                            }
                        }
                    } ?: listOf(),
                    enableScreenTracking = getBooleanOrNull(ENABLE_SCREEN_TRACKING) != false,
                    groupingEnabled = getBooleanOrNull(GROUPING_ENABLED) == true,
                    groupingIdleTime = getIntOrNull(GROUPING_IDLE_TIME) ?: Constants.DEFAULT_GROUPING_IDLE_TIME,
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
            put(GROUPING_ENABLED, groupingEnabled)
            put(GROUPING_IDLE_TIME, groupingIdleTime)
            put(EXPIRATION, expiration)
        }
    }
}