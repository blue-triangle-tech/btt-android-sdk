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
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        printWriter.close();

        final String[] lines = result.toString().split("\\r?\\n");
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!lines[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(lines[i]);
                sb.append("~~");
            }
        }
        sb.append(lines[lines.length - 1].trim());
        final String stacktrace = sb.toString();

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

