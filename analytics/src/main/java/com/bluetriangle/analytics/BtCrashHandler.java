package com.bluetriangle.analytics;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;


public class BtCrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    private String url;

    private String trackerUrl;

    private String sitePrefix;

    private String siteSession;

    private String applicationName;

    Timer crashHitsTimer;

    public BtCrashHandler(String url, String incSitePrefix, String incSiteSession, String trackerUrl,
            String applicationName) {
        this.url = url;
        this.trackerUrl = trackerUrl;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.sitePrefix = incSitePrefix;
        this.siteSession = incSiteSession;
        this.crashHitsTimer = new Timer();
        this.applicationName = applicationName;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        //Log.d("Crash report start", timeStamp);
        this.crashHitsTimer.start();
        this.crashHitsTimer.setPageName("Android%20Crash%20" + getDeviceName());
        this.crashHitsTimer.setTrafficSegmentName("Android%20Crash");
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        String lines[] = stacktrace.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length - 1; i++) {
            //data.length - 1 => to not add separator at the end
            if (!lines[i].matches(" *")) {//empty string are ""; " "; "  "; and so on
                sb.append(lines[i]);
                sb.append("~~");
            }
        }
        sb.append(lines[lines.length - 1].trim());
        stacktrace = sb.toString();
        printWriter.close();
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

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model).replaceAll(" ", "%20");
        } else {
            return capitalize(manufacturer).replaceAll(" ", "%20") + "%20" + model.replaceAll(" ", "%20");
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
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
        Timer crashHitsTimer;

        /**
         * Create a timer runnable to submit the given timer to the given URL
         *
         * @param crashReportUrl the tracker API URL to submit the data to
         * @param sitePrefix     the timer to submit
         */
        CrashReportRunnable(final String crashReportUrl, final String sitePrefix, final String stackTrace,
                final String siteSession, final String timeStamp, Timer crashHitsTimer, String trackerUrl,
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
            final String nStart = this.crashHitsTimer.getField("nst");
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
                final String timerTime = this.crashHitsTimer.getField("pgTm");
                final String siteurl = this.crashReportUrl + "?" +
                        "siteID=" + this.sitePrefix + "&" +
                        "nStart=" + nStart + "&" +
                        "pageName=" + "Android%20Crash%20" + getDeviceName() + "&" +
                        "txnName=" + "Android%20Crash" + "&" +
                        "sessionID=" + this.siteSession + "&" +
                        "pgTm=" + timerTime + "&" +
                        "pageType=" + getDeviceName() + "&" +
                        "AB=" + "Default" + "&" +
                        "DCTR=" + "Default" + "&" +
                        "CmpN=" + "" + "&" +
                        "CmpM=" + "Android" + "&" +
                        "CmpS=" + "Crash";

                //Log.d("Crash URL:", siteurl);
                final URL url = new URL(siteurl);
                //Log.d("Crash URL", String.format("Crash URL: %s", siteurl));
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

        public String getDeviceName() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return capitalize(model).replaceAll(" ", "%20");
            } else {
                return capitalize(manufacturer).replaceAll(" ", "%20") + "%20" + model.replaceAll(" ", "%20");
            }
        }


        private String capitalize(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }
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

