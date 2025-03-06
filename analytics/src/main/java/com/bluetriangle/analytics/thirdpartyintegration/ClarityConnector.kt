package com.bluetriangle.analytics.thirdpartyintegration

import android.app.Application
import com.bluetriangle.analytics.Logger
import com.microsoft.clarity.Clarity
import com.microsoft.clarity.ClarityConfig
import com.microsoft.clarity.models.LogLevel
import kotlin.math.log

internal class ClarityConnector(val application: Application,
                                logger: Logger?,
                                customVariablesAdapter: CustomVariablesAdapter
): ThirdPartyConnector(logger, customVariablesAdapter) {

    private var clarityProjectID: String? = null
    private var clarityEnabled: Boolean = false

    companion object {
        const val CLARITY_PROJECT_ID = "clarityProjectID"
        const val CLARITY_SESSION_URL_CV = "CV0"
    }

    private var isClarityInClasspath = false

    init {
        checkClarityInClassPath()
    }

    private fun checkClarityInClassPath() {
        try {
            Class.forName("com.microsoft.clarity.Clarity")
            isClarityInClasspath = true
        } catch (e: ClassNotFoundException) {
            logger?.error("Clarity not found in classpath")
            isClarityInClasspath = false
        }
    }

    @Synchronized
    override fun start(connectorConfiguration: ConnectorConfiguration) {
        if(!isClarityInClasspath) return

        clarityProjectID = connectorConfiguration.clarityProjectID
        clarityEnabled = connectorConfiguration.clarityEnabled

        Clarity.setOnSessionStartedCallback {
            logger?.debug("Clarity session started: ${Clarity.getCurrentSessionUrl()}")
            setSessionURLToCustomVariable()
        }
        clarityProjectID?.also {
            if(Clarity.isPaused()) {
                logger?.debug("Clarity is paused, resuming")
                Clarity.resume()
                setSessionURLToCustomVariable()
            } else if (clarityEnabled) {
                logger?.debug("Clarity initialized for project ID: $it")
                Clarity.initialize(application, ClarityConfig(it, logLevel = LogLevel.Verbose))
            }
        }
    }

    private fun setSessionURLToCustomVariable() {
        if(!isClarityInClasspath) return

        val sessionURL = Clarity.getCurrentSessionUrl()
        if(sessionURL != null) {
            customVariablesAdapter.setCustomVariable(CLARITY_SESSION_URL_CV, sessionURL)
        }
    }

    @Synchronized
    override fun stop() {
        if(!isClarityInClasspath) return

        Clarity.pause()
        customVariablesAdapter.clearCustomVariable(CLARITY_SESSION_URL_CV)
        logger?.debug("Clarity paused")
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
    override fun payloadFields() = mapOf<String, String>()

}