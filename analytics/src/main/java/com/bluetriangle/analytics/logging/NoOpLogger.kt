package com.bluetriangle.analytics.logging

/**
 * A logger that does not actually log anything
 */
internal class NoOpLogger private constructor() : Logger() {
    override fun log(logLevel: Int, message: String) {}
    override fun log(logLevel: Int, throwable: Throwable?, message: String) {}

    companion object {
        val instance = NoOpLogger()
    }
}