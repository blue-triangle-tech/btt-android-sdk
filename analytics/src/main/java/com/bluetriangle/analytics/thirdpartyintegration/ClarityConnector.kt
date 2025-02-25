package com.bluetriangle.analytics.thirdpartyintegration

import android.app.Application
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

internal class ClarityConnector(val application: Application):ThirdPartyConnector {

    private var clarityProjectID: String? = null
    private var clarityEnabled: Boolean = false

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
            "clarityProjectID" to clarityProjectID
        )
    } else {
        mapOf()
    }

    @Synchronized
    override fun payloadFields() = if(clarityProjectID != null && clarityEnabled) {
        mapOf(
            "clarityURL" to Clarity.getCurrentSessionUrl(),
            "claritySessionID" to Clarity.getCurrentSessionId()
        )
    } else {
        mapOf()
    }
}