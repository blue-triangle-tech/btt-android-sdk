package com.bluetriangle.analytics.sessionmanager

import android.app.Activity
import android.app.Application
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.dynamicconfig.updater.IBTTConfigurationUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class DisabledModeSessionManager(
    private val configuration: BlueTriangleConfiguration,
    private val updater: IBTTConfigurationUpdater
) : ISessionManager {

    private var dummySession = dummySessionData(false)

    private fun dummySessionData(isConfigApplied: Boolean) = SessionData(
        "",
        shouldSampleNetwork = false,
        isConfigApplied = isConfigApplied,
        networkSampleRate = 0.0,
        ignoreScreens = listOf(),
        expiration = 0L
    )

    override val sessionData: SessionData
        get() = dummySession

    private var scope: CoroutineScope? = null

    init {
        initScope()
        updateConfig()
    }

    private fun updateConfig() {
        scope?.launch {
            updater.update()
        }
    }

    override fun endSession() {

    }

    override fun onActivityResumed(activity: Activity) {
        initScope()
        onLaunch()
    }

    override fun onAppMovedToBackground(application: Application) {
        onOffScreen()
        destroyScope()
    }

    private fun onLaunch() {
        updateConfig()
    }

    private fun onOffScreen() {
        dummySession = dummySessionData(false)
    }

    private fun initScope() {
        if(scope?.isActive == true) return

        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    private fun destroyScope() {
        scope?.cancel()
        scope = null
    }

}