package com.bluetriangle.analytics.sessionmanager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.launchtime.AppEventConsumer

class SessionManager(context: Context): AppEventConsumer {

    private var currentSession: SessionData? = null
    private var sessionStore:SessionStore = SharedPrefsSessionStore(context)
    private var expirationDurationInMillis =  10 * 1000

    val sessionId: String
        get() {
            invalidateSessionData()
            return currentSession!!.sessionId
        }

    private fun isSessionExpired():Boolean {
        val sessionData = sessionStore.retrieveSessionData()
        return sessionData == null || System.currentTimeMillis() > sessionData.expiration
    }

    private fun invalidateSessionData():Boolean {
        if(isSessionExpired()) {
            currentSession = generateNewSession().apply {
                sessionStore.storeSessionData(this)
            }
            return true
        }
        currentSession = sessionStore.retrieveSessionData()
        return false
    }

    private fun generateNewSession(): SessionData {
        return SessionData(
            Utils.generateRandomId(),
            getNewExpiration()
        )
    }

    private fun getNewExpiration() = System.currentTimeMillis() + expirationDurationInMillis

    fun onLaunch() {
        if(invalidateSessionData()) {
            Tracker.instance?.updateSession(currentSession!!.sessionId)
        }
    }

    fun onOffScreen() {
        currentSession?.let {
            sessionStore.storeSessionData(
                SessionData(
                    it.sessionId,
                    getNewExpiration()
                )
            )
        }
    }

    override fun onActivityResumed(activity: Activity) {
        onLaunch()
    }

    override fun onAppMovedToBackground(application: Application) {
        onOffScreen()
    }
}