/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.dynamicconfig.updater.IBTTConfigurationUpdater
import com.bluetriangle.analytics.utility.DebugConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class SessionManager(
    context: Context,
    siteId: String,
    private val expirationDurationInMillis: Long,
    private val configurationRepository: IBTTConfigurationRepository,
    private val updater: IBTTConfigurationUpdater,
    private val defaultConfig: BTTRemoteConfiguration
) : ISessionManager {

    private var currentSession: SessionData? = null
        @Synchronized get
        @Synchronized set

    private var sessionStore: SessionStore = SharedPrefsSessionStore(context, siteId)
    private var debugConfig = DebugConfig.getCurrent(context)
    private var scope: CoroutineScope? = null

    override val sessionData: SessionData
        @Synchronized get() {
            invalidateSessionData()
            return currentSession!!
        }

    override fun endSession() {
        sessionStore.clearSessionData()
    }

    init {
        initScope()
        scope?.launch {
            if (!sessionData.isConfigApplied) {
                updater.forceUpdate()
            }
        }
    }

    private fun isSessionExpired(): Boolean {
        val sessionData = sessionStore.retrieveSessionData()
        return sessionData == null || System.currentTimeMillis() > sessionData.expiration
    }

    private fun invalidateSessionData(): Boolean {
        if (debugConfig.newSessionOnLaunch) {
            if (currentSession == null) {
                currentSession = generateNewSession()
            }
            return false
        }
        if (isSessionExpired()) {
            currentSession = generateNewSession().apply {
                sessionStore.storeSessionData(this)
            }
            Tracker.instance?.configuration?.logger?.debug("Session Expired! Updating session to ${currentSession?.sessionId}")
            return true
        }
        if (currentSession == null) {
            currentSession = sessionStore.retrieveSessionData()
        }
        return false
    }

    private fun generateNewSession(): SessionData {
        val config = configurationRepository.get()

        return SessionData(
            Utils.generateRandomId(),
            debugConfig.fullSampleRate || Utils.shouldSample(config.networkSampleRate?:defaultConfig.networkSampleRate!!),
            false,
            config.networkSampleRate?:defaultConfig.networkSampleRate!!,
            config.ignoreScreens,
            getNewExpiration()
        )
    }

    private fun getNewExpiration() = System.currentTimeMillis() + expirationDurationInMillis

    @Synchronized
    private fun onLaunch() {
        Tracker.instance?.updateSession(sessionData)
        scope?.launch {
            if (!sessionData.isConfigApplied) {
                updater.forceUpdate()
            } else {
                updater.update()
            }
        }
    }

    @Synchronized
    private fun onOffScreen() {
        currentSession?.let {
            val newExpirySession = SessionData(
                it.sessionId,
                it.shouldSampleNetwork,
                it.isConfigApplied,
                it.networkSampleRate,
                it.ignoreScreens,
                getNewExpiration()
            )
            sessionStore.storeSessionData(
                newExpirySession
            )
            currentSession = newExpirySession
        }
    }

    override fun onActivityResumed(activity: Activity) {
        initScope()
        onLaunch()
    }

    override fun onAppMovedToBackground(application: Application) {
        onOffScreen()
        destroyScope()
    }

    private fun initScope() {
        destroyScope()
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        observeAndUpdateSession()
    }

    private fun observeAndUpdateSession() {
        scope?.launch {
            configurationRepository.getLiveUpdates().collectLatest { savedConfig ->
                savedConfig?.let { config ->
                    currentSession?.let { session ->
                        if (!session.isConfigApplied) {
                            Tracker.instance?.configuration?.logger?.debug("Applied new configuration $savedConfig to session $session")
                            val sessionData = SessionData(
                                session.sessionId,
                                Utils.shouldSample(config.networkSampleRate?:defaultConfig.networkSampleRate!!),
                                true,
                                config.networkSampleRate?:defaultConfig.networkSampleRate!!,
                                config.ignoreScreens,
                                session.expiration
                            )
                            Tracker.instance?.updateSession(sessionData)
                            sessionStore.storeSessionData(
                                sessionData
                            )
                            currentSession = sessionData
                        }
                    }
                }
            }
        }
    }

    private fun destroyScope() {
        scope?.cancel()
        scope = null
    }
}