package com.bluetriangle.analytics;

import android.os.Process;
import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

/**
 * A thread pool executor for queueing and submitting timers
 */
class TrackerExecutor extends ThreadPoolExecutor {

    private static final int DEFAULT_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 2;
    private static final long KEEP_ALIVE_TIME_MS = 0;
    private static final String THREAD_NAME_PREFIX = "BTT-";

    private final BlueTriangleConfiguration configuration;

    TrackerExecutor(@NonNull BlueTriangleConfiguration configuration) {
        super(DEFAULT_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TrackerThreadFactory());
        this.configuration = configuration;
    }

    /**
     * Builds threads with the given runnable
     */
    private static class TrackerThreadFactory implements ThreadFactory {
        public Thread newThread(@NonNull final Runnable runnable) {
            return new TrackerThread(runnable);
        }
    }

    /**
     * A custom thread which sets priority to background and generates a unique name
     */
    private static class TrackerThread extends Thread {

        private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger(1);

        TrackerThread(final Runnable runnable) {
            super(runnable, THREAD_NAME_PREFIX + SEQUENCE_GENERATOR.getAndIncrement());
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
