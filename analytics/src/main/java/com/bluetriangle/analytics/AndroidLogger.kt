package com.bluetriangle.analytics

import android.util.Log
import com.bluetriangle.analytics.AndroidLogger

internal class AndroidLogger(private val logLevel: Int) : Logger() {
    override fun log(logLevel: Int, message: String, vararg args: Any?) {
        log(logLevel, null, message, *args)
    }

    override fun log(logLevel: Int, throwable: Throwable?, message: String, vararg args: Any?) {
        if (logLevel >= this.logLevel) {
            val formattedMessage = String.format(message, *args)
            when (logLevel) {
                Log.INFO -> Log.i(tag, formattedMessage, throwable)
                Log.WARN -> Log.w(tag, formattedMessage, throwable)
                Log.ERROR -> Log.e(tag, formattedMessage, throwable)
                Log.DEBUG -> Log.d(tag, formattedMessage, throwable)
                else -> Log.d(tag, formattedMessage, throwable)
            }
        }
    }

    companion object {
        private const val tag = "BlueTriangle"
    }
}