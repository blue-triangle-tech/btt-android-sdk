package com.bluetriangle.analytics.sessionmanager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class SessionManager(
    context: Context,
    private val expirationDurationInMillis: Long,
    private val configurationRepository: IBTTConfigurationRepository
): AppEventConsumer {

    private var currentSession: SessionData? = null
        @Synchronized get
        @Synchronized set

    private var sessionStore:SessionStore = SharedPrefsSessionStore(context)

    val sessionData: SessionData
        @Synchronized get() {
            invalidateSessionData()
            return currentSession!!
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
            Tracker.instance?.configuration?.logger?.debug("Session Expired! Updating session to ${currentSession?.sessionId}")
            return true
        }
        if(currentSession == null) {
            sessionStore.retrieveSessionData()?.apply {
                val oldSession = SessionData(
                    sessionId,
                    shouldSampleNetwork,
                    false,
                    expiration
                ).apply {
                    sessionStore.storeSessionData(this)
                }
                currentSession = oldSession
            }
        }
        return false
    }

    private fun generateNewSession(): SessionData {
        return SessionData(
            Utils.generateRandomId(),
            Utils.shouldSample(getNetworkSampleRate()),
            true,
            getNewExpiration()
        )
    }

    private fun getNetworkSampleRate(): Double {
        val config = configurationRepository.get() ?: return Constants.DEFAULT_NETWORK_SAMPLE_RATE
        return config.networkSampleRate
    }

    private fun getNewExpiration() = System.currentTimeMillis() + expirationDurationInMillis

    @Synchronized fun onLaunch() {
        Tracker.instance?.updateSession(sessionData.sessionId)
    }

    @Synchronized fun onOffScreen() {
        currentSession?.let {
            sessionStore.storeSessionData(
                SessionData(
                    it.sessionId,
                    it.shouldSampleNetwork,
                    false,
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