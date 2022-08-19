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
    abstract fun log(logLevel: Int, message: String, vararg args: Any?)

    /**
     * Log a throwable and message at the given log level
     *
     * @param logLevel the log level
     * @param throwable the throwable to log
     * @param message the message to log
     * @param args optional args to format the message
     */
    abstract fun log(logLevel: Int, throwable: Throwable?, message: String, vararg args: Any?)

    /**
     * Log a DEBUG message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun debug(message: String, vararg args: Any?) {
        log(Log.DEBUG, message, *args)
    }

    /**
     * Log an INFO message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun info(message: String, vararg args: Any?) {
        log(Log.INFO, message, *args)
    }

    /**
     * Log a WARN message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun warn(message: String, vararg args: Any?) {
        log(Log.WARN, message, *args)
    }

    /**
     * Log a WARN message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    fun warn(throwable: Throwable?, message: String, vararg args: Any?) {
        log(Log.WARN, throwable, message, *args)
    }

    /**
     * Log an ERROR message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    fun error(message: String, vararg args: Any?) {
        log(Log.ERROR, message, *args)
    }

    /**
     * Log an ERROR message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    fun error(throwable: Throwable?, message: String, vararg args: Any?) {
        log(Log.ERROR, throwable, message, *args)
    }
}