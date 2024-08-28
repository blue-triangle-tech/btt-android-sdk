package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.Tracker
import org.json.JSONObject

internal data class SessionData(
    val sessionId:String,
    val expiration:Long
) {
    companion object {

        private const val SESSION_ID = "sessionId"
        private const val EXPIRATION = "expiration"

        fun JSONObject.toSessionData():SessionData? {
            try {
                return SessionData(
                    sessionId = getString(SESSION_ID),
                    expiration = getLong(EXPIRATION)
                )
            } catch (e: Exception) {
                Tracker.instance?.configuration?.logger?.error("Error while parsing session data: ${e::class.simpleName}(\"${e.message}\")")
                return null
            }
        }

        fun SessionData.toJsonObject() = JSONObject().apply {
            put(SESSION_ID, sessionId)
            put(EXPIRATION, expiration)
        }
    }
}