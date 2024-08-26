package com.bluetriangle.analytics.sessionmanager

import android.content.Context
import com.bluetriangle.analytics.sessionmanager.SessionData.Companion.toSessionData
import com.bluetriangle.analytics.sessionmanager.SessionData.Companion.toJsonObject
import org.json.JSONObject

class SharedPrefsSessionStore(val context: Context):SessionStore {

    companion object {
        private const val SESSION_STORE = "session_store"
        private const val SESSION_DATA = "session_data"
    }

    private val storePrefs = context.getSharedPreferences(SESSION_STORE, Context.MODE_PRIVATE)

    override fun storeSessionData(sessionData: SessionData) {
        storePrefs.edit()
            .putString(SESSION_DATA, sessionData.toJsonObject().toString())
            .commit()
    }

    override fun retrieveSessionData(): SessionData? {
        val sessionDataJSON = storePrefs.getString(SESSION_DATA, null)?:return null

        return JSONObject(sessionDataJSON).toSessionData()
    }

}