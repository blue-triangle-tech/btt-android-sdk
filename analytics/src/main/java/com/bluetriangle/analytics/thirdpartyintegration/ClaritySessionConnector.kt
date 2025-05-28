package com.bluetriangle.analytics.thirdpartyintegration

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Tracker
import java.lang.reflect.Method

class ClaritySessionConnector(val logger: Logger?) {

    companion object {
        private const val CLARITY_CLASS = "com.microsoft.clarity.Clarity"
        private const val CLARITY_SESSION_URL_METHOD = "getCurrentSessionUrl"
        private const val BTT_CLARITY_SESSION_CV = "CV0"
    }

    private val getCurrentSessionUrl = getClarityClass()?.getCurrentSessionUrlMethod()

    fun refreshClaritySessionUrlCustomVariable() {
        if(getCurrentSessionUrl == null) return

        val sessionUrl = getCurrentSessionUrl.invoke(null) as? String?

        if(sessionUrl == null) {
            Tracker.instance?.clearCustomVariable(BTT_CLARITY_SESSION_CV)
        } else {
            Tracker.instance?.setCustomVariable(BTT_CLARITY_SESSION_CV, sessionUrl)
        }
    }

    private fun getClarityClass(): Class<*>? {
        return try {
            Class.forName(CLARITY_CLASS)
        } catch (e: Exception) {
            logger?.error("Clarity not found in classpath")
            null
        }
    }

    private fun Class<*>?.getCurrentSessionUrlMethod(): Method? {
        return try {
            this?.getDeclaredMethod(CLARITY_SESSION_URL_METHOD)
        } catch (e: Exception) {
            logger?.error("$CLARITY_SESSION_URL_METHOD not found in Clarity")
            null
        }
    }

}