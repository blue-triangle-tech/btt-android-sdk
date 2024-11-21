/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

import android.app.Activity
import android.app.Application
import android.content.Context
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.dynamicconfig.updater.IBTTConfigurationUpdater
import com.bluetriangle.analytics.launchtime.AppEventConsumer
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
    private val updater: IBTTConfigurationUpdater
) : AppEventConsumer {

    private var currentSession: SessionData? = null
        @Synchronized get
        @Synchronized set

    private var sessionStore: SessionStore = SharedPrefsSessionStore(context, siteId)
    private var debugConfig = DebugConfig.getCurrent(context)
    private var scope: CoroutineScope? = null

    val sessionData: SessionData
        @Synchronized get() {
            invalidateSessionData()
            return currentSession!!
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
            ?: BTTSavedRemoteConfiguration(Constants.DEFAULT_NETWORK_SAMPLE_RATE, 0)

        return SessionData(
            Utils.generateRandomId(),
            debugConfig.fullSampleRate || Utils.shouldSample(config.networkSampleRate),
            false,
            config.networkSampleRate,
            getNewExpiration()
        )
    }

    private fun getNewExpiration() = System.currentTimeMillis() + expirationDurationInMillis

    @Synchronized
    fun onLaunch() {
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
    fun onOffScreen() {
        currentSession?.let {
            sessionStore.storeSessionData(
                SessionData(
                    it.sessionId,
                    it.shouldSampleNetwork,
                    it.isConfigApplied,
                    it.networkSampleRate,
                    getNewExpiration()
                )
            )
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
                                Utils.shouldSample(config.networkSampleRate),
                                true,
                                config.networkSampleRate,
                                session.expiration
                            )
                            Tracker.instance?.updateSession(sessionData)
                            sessionStore.storeSessionData(
                                sessionData
                            )
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