package com.bluetriangle.analytics.thirdpartyintegration

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Tracker
import java.lang.reflect.Method

class ClaritySessionConnector(val logger: Logger?) {

    companion object {
        private const val CLARITY_CLASS = "com.microsoft.clarity.Clarity"
        private const val CLARITY_SESSION_URL_METHOD = "getCurrentSessionUrl"
        private const val CLARITY_SESSION_CV = "CV0"
    }

    private val clarityClass = classForName(CLARITY_CLASS)
    private val getCurrentSessionUrl = clarityClass?.methodForName(CLARITY_SESSION_URL_METHOD)

    fun refreshClaritySessionUrlCustomVariable() {
        val sessionUrl = getCurrentSessionUrl?.invoke(null) as? String?

        if(sessionUrl == null) {
            Tracker.instance?.clearCustomVariable(CLARITY_SESSION_CV)
        } else {
            Tracker.instance?.setCustomVariable(CLARITY_SESSION_CV, sessionUrl)
        }
    }

    private fun classForName(className: String): Class<*>? {
        return try {
            Class.forName(className)
        } catch (e: Exception) {
            logger?.error("${className.split(".").lastOrNull()} not found in classpath")
            null
        }
    }

    private fun Class<*>?.methodForName(methodName: String): Method? {
        return try {
            this?.getDeclaredMethod(methodName)
        } catch (e: Exception) {
            logger?.error("$methodName not exists in class ${this?.simpleName}")
            null
        }
    }

}