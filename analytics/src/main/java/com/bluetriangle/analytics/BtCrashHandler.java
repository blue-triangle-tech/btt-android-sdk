package com.bluetriangle.analytics;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


public class BtCrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String FIELD_NAVIGATION_START = "nStart";
    public static final String FIELD_SESSION_ID = "sessionID";

    private final Thread.UncaughtExceptionHandler defaultUEH;

    private final String url;

    private final String trackerUrl;

    private final String sitePrefix;

    private final String siteSession;

    private final String applicationName;

    Timer crashHitsTimer;

    public BtCrashHandler(final String url, final String sitePrefix, final String siteSession, final String trackerUrl,
            final String applicationName) {
        this.url = url;
        this.trackerUrl = trackerUrl;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.sitePrefix = sitePrefix;
        this.siteSession = siteSession;
        this.crashHitsTimer = new Timer();
        this.applicationName = applicationName;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        //Log.d("Crash report start", timeStamp);

        this.crashHitsTimer.start();

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

        if (url != null) {
            try {
                sendToServer(stacktrace, timeStamp);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }

        defaultUEH.uncaughtException(t, e);
        Log.d("btt exception:", e.getMessage());
    }

    private void sendToServer(String stacktrace, String timeStamp) throws InterruptedException {
        Thread thread = new Thread(
                new CrashReportRunnable(this.url, this.sitePrefix, stacktrace, this.siteSession, timeStamp,
                        this.crashHitsTimer, this.trackerUrl, this.applicationName));
        thread.start();
        thread.join();

    }

    /**
     * A runnable which submits a crash report to the given API URL
     */
    static class CrashReportRunnable implements Runnable {

        /**
         * Tracker URL to submit crash report to
         */
        final String crashReportUrl;

        /**
         * Tracker URL to submit timer to
         */
        final String trackerUrl;

        /**
         * The users site prefix
         */
        final String sitePrefix;

        /**
         * This is the crash report
         */
        final String stackTrace;

        /**
         * This is the user session
         */
        final String siteSession;

        /**
         * This is the epoch when the error happened
         */
        final String timeStamp;

        /**
         * This is the name of the application
         */
        final String applicationName;

        /**
         * This is the epoch when the error happened
         */
        final Timer crashHitsTimer;

        /**
         * Create a timer runnable to submit the given timer to the given URL
         *
         * @param crashReportUrl the tracker API URL to submit the data to
         * @param sitePrefix     the timer to submit
         */
        CrashReportRunnable(final String crashReportUrl, final String sitePrefix, final String stackTrace,
                final String siteSession, final String timeStamp, final Timer crashHitsTimer, final String trackerUrl,
                final String applicationName) {
            super();
            this.crashReportUrl = crashReportUrl;
            this.trackerUrl = trackerUrl;
            this.sitePrefix = sitePrefix;
            this.stackTrace = stackTrace;
            this.siteSession = siteSession;
            this.timeStamp = timeStamp;
            this.crashHitsTimer = crashHitsTimer;
            this.applicationName = applicationName;
        }

        @Override
        public void run() {
            //Log.d("BTT session", this.siteSession);
            HttpsURLConnection connection = null;
            HttpsURLConnection connectionHits = null;
            this.crashHitsTimer.end();
            final Tracker tracker = Tracker.getInstance();
            this.crashHitsTimer.setFields(tracker.globalFields);

            //first submit the hits data to the portal
            try {
                final URL urlHits = new URL(this.trackerUrl);
                //Log.d("Tracker URL", String.format("Tracker URL: %s", this.trackerUrl));
                connectionHits = (HttpsURLConnection) urlHits.openConnection();
                connectionHits.setRequestMethod("POST");
                connectionHits.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connectionHits.setDoOutput(true);
                connectionHits.setDoInput(false);
                final DataOutputStream dataOutputStreamHits = new DataOutputStream(connectionHits.getOutputStream());
                dataOutputStreamHits.write(buildBase64EncodedJsonForHits());
                dataOutputStreamHits.close();

                final int statusCodeHits = connectionHits.getResponseCode();
                if (statusCodeHits >= 300) {
                    BufferedReader bufferedReaderHits = new BufferedReader(
                            new InputStreamReader(connectionHits.getErrorStream()));
                    StringBuilder builderHits = new StringBuilder();
                    String line = bufferedReaderHits.readLine();
                    while (line != null) {
                        builderHits.append(line);
                        line = bufferedReaderHits.readLine();
                    }
                    bufferedReaderHits.close();
                    final String responseBody = builderHits.toString();
                    Log.e("BTT Crash Hits Timer",
                            String.format("Server Error submitting crash report: %s - %s", statusCodeHits,
                                    responseBody));
                }
                connectionHits.getHeaderField(0);

            } catch (Exception e) {
                Log.e("BTT Crash Hits Timer",
                        String.format("Android Error submitting crash report: %s", e.getMessage()), e);
            } finally {
                if (connectionHits != null) {
                    connectionHits.disconnect();
                }
            }
            Log.d("BTT Crash Hits Timer", "crash report submitted successfully");

            // send crash data
            try {
                final String deviceName = Utils.getDeviceName();

                final String siteUrl = Uri.parse(this.crashReportUrl)
                        .buildUpon()
                        .appendQueryParameter(Timer.FIELD_SITE_ID, sitePrefix)
                        .appendQueryParameter(FIELD_NAVIGATION_START, crashHitsTimer.getField(Timer.FIELD_NST))
                        .appendQueryParameter(Timer.FIELD_PAGE_NAME,
                                crashHitsTimer.getField(Timer.FIELD_PAGE_NAME, "Android Crash " + deviceName))
                        .appendQueryParameter(Timer.FIELD_TRAFFIC_SEGMENT_NAME,
                                crashHitsTimer.getField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, "Android Crash"))
                        .appendQueryParameter(Timer.FIELD_NATIVE_OS,
                                crashHitsTimer.getField(Timer.FIELD_NATIVE_OS, "Android"))
                        .appendQueryParameter(Timer.FIELD_DEVICE, crashHitsTimer.getField(Timer.FIELD_DEVICE, "Mobile"))
                        .appendQueryParameter(Timer.FIELD_BROWSER, Tracker.BROWSER)
                        .appendQueryParameter(FIELD_SESSION_ID, siteSession)
                        .appendQueryParameter(Timer.FIELD_PAGE_TIME, crashHitsTimer.getField("pgTm"))
                        .appendQueryParameter(Timer.FIELD_CONTENT_GROUP_NAME,
                                crashHitsTimer.getField(Timer.FIELD_CONTENT_GROUP_NAME, deviceName))
                        .appendQueryParameter(Timer.FIELD_AB_TEST_ID,
                                crashHitsTimer.getField(Timer.FIELD_AB_TEST_ID, "Default"))
                        .appendQueryParameter(Timer.FIELD_DATACENTER,
                                crashHitsTimer.getField(Timer.FIELD_DATACENTER, "Default"))
                        .appendQueryParameter(Timer.FIELD_CAMPAIGN_NAME,
                                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_NAME, ""))
                        .appendQueryParameter(Timer.FIELD_CAMPAIGN_MEDIUM,
                                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_MEDIUM, "Android"))
                        .appendQueryParameter(Timer.FIELD_CAMPAIGN_SOURCE,
                                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_SOURCE, "Crash"))
                        .build().toString();

                final URL url = new URL(siteUrl);
                //Log.d("Crash URL", String.format("Crash URL: %s", siteUrl));
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setDoOutput(true);
                connection.setDoInput(false);
                final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(buildBase64EncodedJson(this.stackTrace, this.timeStamp, this.applicationName));
                dataOutputStream.close();

                final int statusCode = connection.getResponseCode();
                if (statusCode >= 300) {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream()));
                    StringBuilder builder = new StringBuilder();
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = bufferedReader.readLine();
                    }
                    bufferedReader.close();
                    final String responseBody = builder.toString();
                    Log.e("BTT Crash Reporter",
                            String.format("Server Error submitting crash report: %s - %s", statusCode, responseBody));
                }
                connection.getHeaderField(0);

            } catch (Exception e) {
                Log.e("BTT Crash Reporter", String.format("Android Error submitting crash report: %s", e.getMessage()),
                        e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            Log.d("BTT Crash Reporter", "crash report submitted successfully");
        }

        /**
         * Build the base 64 encoded data to POST to the API
         *
         * @return base 64 encoded JSON payload
         * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
         */
        private byte[] buildBase64EncodedJson(final String stacktrace, final String timeStamp,
                final String applicationName) throws UnsupportedEncodingException {
            final HashMap<String, String> crashReport = new HashMap<>();
            crashReport.put("msg", stacktrace);
            crashReport.put("eTp", "NativeAppCrash");
            crashReport.put("eCnt", "1");
            crashReport.put("url", applicationName);
            crashReport.put("line", "1");
            crashReport.put("col", "1");
            crashReport.put("time", timeStamp);

            final JSONObject jsonCrashReport = new JSONObject(crashReport);
            final JSONArray crashDataArray = new JSONArray(Collections.singletonList(jsonCrashReport));
            return Base64.encode(crashDataArray.toString().getBytes("UTF-8"), Base64.DEFAULT);
        }

        /**
         * Builds a JSON string of the timer's fields
         *
         * @return a string of JSON representing the timer
         */
        private String buildJson() {
            final JSONObject data = new JSONObject(this.crashHitsTimer.getFields());
            //Log.d("crash hits data sent:", data.toString());
            return data.toString();
        }

        /**
         * Build the base 64 encoded data to POST to the API
         *
         * @return base 64 encoded JSON payload
         * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
         */
        private byte[] buildBase64EncodedJsonForHits() throws UnsupportedEncodingException {
            return Base64.encode(buildJson().getBytes("UTF-8"), Base64.DEFAULT);
        }

    }
}

