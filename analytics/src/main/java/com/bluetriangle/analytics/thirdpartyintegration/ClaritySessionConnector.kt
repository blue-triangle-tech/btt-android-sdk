package com.bluetriangle.analytics.thirdpartyintegration

import com.bluetriangle.analytics.Logger
import java.lang.reflect.Method

class ClaritySessionConnector(val logger: Logger?) {

    companion object {
        private const val CLARITY_CLASS = "com.microsoft.clarity.Clarity"
        private const val CLARITY_SESSION_URL_METHOD = "getCurrentSessionUrl"
    }

    private val clarityClass = classForName(CLARITY_CLASS)
    private val getCurrentSessionUrl = clarityClass?.methodForName(CLARITY_SESSION_URL_METHOD)

    fun getSessionUrl():String? {
        return getCurrentSessionUrl?.invoke(null) as? String?
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