package com.bluetriangle.analytics.clarity

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.sdkevents.SDKConfiguration
import com.bluetriangle.analytics.sdkevents.SDKEventsListener
import com.bluetriangle.analytics.sdkevents.SDKEventsManager
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig

object ClarityInitializer: SDKEventsListener {

    fun init() {
        SDKEventsManager.registerConfigurationEventListener(this)
    }

    override fun onConfigured(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onConfigured::${configuration}")
    }

    override fun onEnabled(application: Application, configuration: SDKConfiguration) {
        configuration.clarityProjectID?.let {
            Clarity.initialize(application, ClarityConfig(it))
            Clarity.setCustomSessionId(configuration.sessionID)
        }
        Clarity.resume()
        Log.d("BlueTriangle", "ClarityInitializer::onEnabled::${configuration}")
    }

    override fun onDisabled(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onDisabled::${configuration}")
        Clarity.pause()
    }

    override fun onSessionChanged(application: Application, configuration: SDKConfiguration) {
        Log.d("BlueTriangle", "ClarityInitializer::onSessionChanged::${configuration}")
        Clarity.setCustomSessionId(configuration.sessionID)
    }

}