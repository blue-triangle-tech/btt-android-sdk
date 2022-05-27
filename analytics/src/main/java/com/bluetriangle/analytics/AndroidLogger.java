package com.bluetriangle.analytics;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final public class AndroidLogger extends Logger {
    private static final String tag = "BlueTriangle";
    private final int logLevel;

    public AndroidLogger(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    void log(int logLevel, @NonNull String message, @Nullable Object... args) {
        log(logLevel, null, message, args);
    }

    @Override
    void log(int logLevel, @Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        if (logLevel >= this.logLevel) {
            final String formattedMessage = String.format(message, args);
            switch (logLevel) {
                case Log.INFO:
                    Log.i(tag, formattedMessage, throwable);
                    break;
                case Log.WARN:
                    Log.w(tag, formattedMessage, throwable);
                    break;
                case Log.ERROR:
                    Log.e(tag, formattedMessage, throwable);
                    break;
                case Log.DEBUG:
                default:
                    Log.d(tag, formattedMessage, throwable);
                    break;
            }
        }
    }
}
