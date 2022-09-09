package com.bluetriangle.analytics

import android.util.Log
import com.bluetriangle.analytics.AndroidLogger

internal class AndroidLogger(private val logLevel: Int) : Logger() {
    override fun log(logLevel: Int, message: String) {
        log(logLevel, null, message)
    }

    override fun log(logLevel: Int, throwable: Throwable?, message: String) {
        if (logLevel >= this.logLevel) {
            when (logLevel) {
                Log.INFO -> Log.i(tag, message, throwable)
                Log.WARN -> Log.w(tag, message, throwable)
                Log.ERROR -> Log.e(tag, message, throwable)
                Log.DEBUG -> Log.d(tag, message, throwable)
                else -> Log.d(tag, message, throwable)
            }
        }
    }

    companion object {
        private const val tag = "BlueTriangle"
    }
}