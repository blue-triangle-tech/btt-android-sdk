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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread pool executor for queueing and submitting timers
 */
class TrackerExecutor extends ThreadPoolExecutor {

    private static final String LOG_TAG = "BTT_TIMER";

    private static final int DEFAULT_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 2;
    private static final long KEEP_ALIVE_TIME_MS = 0;
    private static final String THREAD_NAME_PREFIX = "BTT-";

    TrackerExecutor() {
        super(DEFAULT_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME_MS, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TrackerThreadFactory());
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

    /**
     * A runnable which submits a timer to the given API URL
     */
    static class TimerRunnable implements Runnable {

        /**
         * Tracker URL to submit timers to
         */
        final String trackerUrl;

        /**
         * The timer to submit
         */
        final Timer timer;

        /**
         * Create a timer runnable to submit the given timer to the given URL
         *
         * @param trackerUrl the tracker API URL to submit the data to
         * @param timer      the timer to submit
         */
        TimerRunnable(final String trackerUrl, final Timer timer) {
            super();
            this.trackerUrl = trackerUrl;
            this.timer = timer;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            try {
                final URL url = new URL(trackerUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setDoOutput(true);
                final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(buildBase64EncodedJson());
                dataOutputStream.close();

                final int statusCode = connection.getResponseCode();
                if (statusCode >= 300) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    StringBuilder builder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    final String responseBody = builder.toString();
                    Log.e(LOG_TAG, String.format("Server Error submitting %s: %s - %s", timer, statusCode, responseBody));
                }
                connection.getHeaderField(0);

            } catch (Exception e) {
                Log.e(LOG_TAG, String.format("Android Error submitting %s: %s", timer, e.getMessage()), e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.d(LOG_TAG, String.format("%s submitted successfully", timer));
        }

        /**
         * Builds a JSON string of the timer's fields
         *
         * @return a string of JSON representing the timer
         */
        private String buildJson() {
            final JSONObject data = new JSONObject(timer.getFields());
            return data.toString();
        }

        /**
         * Build the base 64 encoded data to POST to the API
         *
         * @return base 64 encoded JSON payload
         * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
         */
        private byte[] buildBase64EncodedJson() throws UnsupportedEncodingException {
            return Base64.encode(buildJson().getBytes("UTF-8"), Base64.DEFAULT);
        }

    }

}
