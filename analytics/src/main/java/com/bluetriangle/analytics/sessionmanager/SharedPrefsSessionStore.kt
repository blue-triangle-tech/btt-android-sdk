/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import android.content.Context
import androidx.core.content.edit
import com.bluetriangle.analytics.sessionmanager.SessionData.Companion.toJsonObject
import com.bluetriangle.analytics.sessionmanager.SessionData.Companion.toSessionData
import org.json.JSONObject

internal class SharedPrefsSessionStore(val context: Context, val siteId: String):SessionStore {

    companion object {
        private const val SESSION_STORE = "session_store"
        private const val SESSION_DATA = "session_data"
    }

    private val storePrefs = context.getSharedPreferences(SESSION_STORE, Context.MODE_PRIVATE)
    private val sessionDataKey:String = "${SESSION_DATA}_${siteId}"

    override fun storeSessionData(sessionData: SessionData) {
        storePrefs.edit {
            putString(sessionDataKey, sessionData.toJsonObject().toString())
        }
    }

    override fun retrieveSessionData(): SessionData? {
        val sessionDataJSON = storePrefs.getString(sessionDataKey, null)?:return null

        return JSONObject(sessionDataJSON).toSessionData()
    }

    override fun clearSessionData() {
        storePrefs.edit {
            remove(sessionDataKey)
        }
    }
}