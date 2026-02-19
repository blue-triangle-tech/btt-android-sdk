package com.bluetriangle.analytics

import android.util.Log

/**
 * abstract logger interface for handling logging
 */
abstract class Logger {
    /**
     * Log a message at the given log level
     *
     * @param logLevel the log level
     * @param message the message
     * @param args optional args to format the message
     */
    abstract fun log(logLevel: Int, message: String)

    /**
     * Log a throwable and message at the given log level
     *
     * @param logLevel the log level
     * @param throwable the throwable to log
     * @param message the message to log
     * @param args optional args to format the message
     */
    abstract fun log(logLevel: Int, throwable: Throwable?, message: String)


    /**
     * Log a VERBOSE message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun verbose(message: String) {
        log(Log.VERBOSE, message)
    }

    /**
     * Log a DEBUG message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun debug(message: String) {
        log(Log.DEBUG, message)
    }

    /**
     * Log an INFO message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun info(message: String) {
        log(Log.INFO, message)
    }

    /**
     * Log a WARN message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun warn(message: String) {
        log(Log.WARN, message)
    }

    /**
     * Log a WARN message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    fun warn(throwable: Throwable?, message: String) {
        log(Log.WARN, throwable, message)
    }

    /**
     * Log an ERROR message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun error(message: String) {
        log(Log.ERROR, message)
    }

    /**
     * Log an ERROR message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    fun error(throwable: Throwable?, message: String) {
        log(Log.ERROR, throwable, message)
    }
}