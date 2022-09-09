package com.bluetriangle.analytics;

import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


final class BtCrashHandler implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultUEH;

    private final BlueTriangleConfiguration configuration;

    private Timer crashHitsTimer;

    public BtCrashHandler(@NonNull final BlueTriangleConfiguration configuration) {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.configuration = configuration;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void uncaughtException(@NonNull Thread t, Throwable e) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        this.crashHitsTimer = new Timer().start();
        final String stacktrace = Utils.exceptionToStacktrace(null, e);

        try {
            sendToServer(stacktrace, timeStamp);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }

        defaultUEH.uncaughtException(t, e);
    }

    private void sendToServer(final String stacktrace, final String timeStamp) throws InterruptedException {
        Thread thread = new Thread(new CrashRunnable(configuration, stacktrace, timeStamp, crashHitsTimer));
        thread.start();
        thread.join();
    }


}

