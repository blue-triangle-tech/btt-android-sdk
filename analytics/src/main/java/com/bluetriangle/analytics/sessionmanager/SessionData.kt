/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.Tracker
import org.json.JSONObject

internal data class SessionData(
    val sessionId:String,
    val shouldSampleNetwork: Boolean,
    val isConfigApplied: Boolean,
    val networkSampleRate: Double,
    val expiration:Long
) {
    companion object {
        private const val SESSION_ID = "sessionId"
        private const val EXPIRATION = "expiration"
        private const val SHOULD_SAMPLE_NETWORK = "shouldSampleNetwork"
        private const val IS_CONFIG_APPLIED = "isConfigApplied"
        private const val NETWORK_SAMPLE_RATE = "networkSampleRate"

        internal fun JSONObject.toSessionData():SessionData? {
            try {
                return SessionData(
                    sessionId = getString(SESSION_ID),
                    shouldSampleNetwork = getBoolean(SHOULD_SAMPLE_NETWORK),
                    isConfigApplied = getBoolean(IS_CONFIG_APPLIED),
                    networkSampleRate = getDouble(NETWORK_SAMPLE_RATE),
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
            put(EXPIRATION, expiration)
        }
    }
}