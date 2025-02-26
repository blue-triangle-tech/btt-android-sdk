package com.bluetriangle.analytics.thirdpartyintegration

import android.app.Application
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

internal class ClarityConnector(val application: Application):ThirdPartyConnector {

    private var clarityProjectID: String? = null
    private var clarityEnabled: Boolean = false

    companion object {
        const val CLARITY_SESSION_ID = "claritySessionID"
        const val CLARITY_PROJECT_ID = "clarityProjectID"
        const val CLARITY_SESSION_URL = "claritySessionURL"
    }

    @Synchronized
    override fun start() {
        clarityProjectID?.also {
            if(Clarity.isPaused()) {
                Clarity.resume()
            } else if (clarityEnabled) {
                Clarity.initialize(application, ClarityConfig(it, logLevel = LogLevel.Verbose))
            }
        }
    }

    @Synchronized
    override fun stop() {
        Clarity.pause()
    }

    @Synchronized
    override fun setConfiguration(connectorConfiguration: ConnectorConfiguration) {
        clarityProjectID = connectorConfiguration.clarityProjectID
        clarityEnabled = connectorConfiguration.clarityEnabled
    }

    @Synchronized
    override fun nativeAppPayloadFields() = if(clarityProjectID != null && clarityEnabled) {
        mapOf(
            CLARITY_PROJECT_ID to clarityProjectID
        )
    } else {
        mapOf()
    }

    @Synchronized
    override fun payloadFields() = if(clarityProjectID != null && clarityEnabled) {
        mapOf(
            CLARITY_SESSION_URL to Clarity.getCurrentSessionUrl(),
            CLARITY_SESSION_ID to Clarity.getCurrentSessionId()
        )
    } else {
        mapOf()
    }
}