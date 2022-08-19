package com.bluetriangle.analytics

import com.bluetriangle.analytics.NoOpLogger

/**
 * A logger that does not actually log anything
 */
internal class NoOpLogger private constructor() : Logger() {
    override fun log(logLevel: Int, message: String, vararg args: Any?) {}
    override fun log(logLevel: Int, throwable: Throwable?, message: String, vararg args: Any?) {}

    companion object {
        val instance = NoOpLogger()
    }
}