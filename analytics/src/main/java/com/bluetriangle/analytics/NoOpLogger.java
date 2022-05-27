package com.bluetriangle.analytics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A logger that does not actually log anything
 */
final public class NoOpLogger extends Logger {

    private static final NoOpLogger instance = new NoOpLogger();

    public static NoOpLogger getInstance() {
        return instance;
    }

    private NoOpLogger() {}

    @Override
    void log(int logLevel, @NonNull String message, @Nullable Object... args) {}

    @Override
    void log(int logLevel, @Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {}
}
