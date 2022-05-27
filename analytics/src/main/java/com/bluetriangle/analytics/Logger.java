package com.bluetriangle.analytics;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * abstract logger interface for handling logging
 */
public abstract class Logger {
    /**
     * Log a message at the given log level
     *
     * @param logLevel the log level
     * @param message the message
     * @param args optional args to format the message
     */
    abstract void log(int logLevel, @NonNull String message, @Nullable Object... args);

    /**
     * Log a throwable and message at the given log level
     *
     * @param logLevel the log level
     * @param throwable the throwable to log
     * @param message the message to log
     * @param args optional args to format the message
     */
    abstract void log(int logLevel, @Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

    /**
     * Log a DEBUG message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    void debug(@NonNull String message, @Nullable Object... args) {
        log(Log.DEBUG, message, args);
    }

    /**
     * Log an INFO message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    void info(@NonNull String message, @Nullable Object... args) {
        log(Log.INFO, message, args);
    }

    /**
     * Log a WARN message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    void warn(@NonNull String message, @Nullable Object... args) {
        log(Log.WARN, message, args);
    }

    /**
     * Log a WARN message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    void warn(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        log(Log.WARN, throwable, message, args);
    }

    /**
     * Log an ERROR message
     *
     * @param message message to log
     * @param args optional args to format the message
     */
    void error(@NonNull String message, @Nullable Object... args) {
        log(Log.ERROR, message, args);
    }

    /**
     * Log an ERROR message
     *
     * @param throwable the throwable to log
     * @param message message to log
     * @param args optional args to format the message
     */
    void error(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        log(Log.ERROR, throwable, message, args);
    }
}
