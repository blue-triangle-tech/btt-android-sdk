package com.bluetriangle.analytics.thirdpartyintegration

import android.app.Application
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel

internal class ClarityConnector(val application: Application):ThirdPartyConnector {

    private var clarityProjectID: String? = null
    private var clarityEnabled: Boolean = false

    override fun start() {
        clarityProjectID?.also {
            if(Clarity.isPaused()) {
                Clarity.resume()
            } else if (clarityEnabled) {
                Clarity.initialize(application, ClarityConfig(it, logLevel = LogLevel.Verbose))
            }
        }
    }

    override fun stop() {
        Clarity.pause()
    }

    override fun setConfiguration(sdkConfiguration: SDKConfiguration) {
        clarityProjectID = sdkConfiguration.clarityProjectID
        clarityEnabled = sdkConfiguration.clarityEnabled
    }

    override fun payloadFields() = if(clarityProjectID != null && clarityEnabled) {
        mapOf(
            "clarityProjectID" to clarityProjectID,
            "clarityURL" to Clarity.getCurrentSessionUrl()
        )
    } else {
        mapOf()
    }
}