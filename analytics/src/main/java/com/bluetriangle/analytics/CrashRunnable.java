package com.bluetriangle.analytics;

import android.net.Uri;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

final class CrashRunnable implements Runnable {

    private static final String FIELD_ERROR_NAVIGATION_START = "nStart";
    private static final String FIELD_ERROR_SESSION_ID = "sessionID";

    /**
     * Tracker configuration
     */
    final BlueTriangleConfiguration configuration;

    /**
     * This is the crash report
     */
    final String stackTrace;

    /**
     * This is the epoch when the error happened
     */
    final String timeStamp;

    /**
     * Timer to track crash reporting
     */
    final Timer crashHitsTimer;

    /**
     * Create a timer runnable to submit the given timer to the given URL
     *
     * @param configuration the tracker configuration
     * @param stackTrace     the error stack trace
     * @param timeStamp time stamp when error occurred
     * @param crashHitsTimer crash hit timer
     */
    CrashRunnable(final BlueTriangleConfiguration configuration, final String stackTrace, final String timeStamp, final Timer crashHitsTimer) {
        super();
        this.configuration = configuration;
        this.stackTrace = stackTrace;
        this.timeStamp = timeStamp;
        this.crashHitsTimer = crashHitsTimer;
    }

    @Override
    public void run() {
        final String deviceName = Utils.getDeviceName();

        HttpsURLConnection connection = null;
        HttpsURLConnection connectionHits = null;
        this.crashHitsTimer.end();
        this.crashHitsTimer.setPageName("Android Crash " + deviceName);
        final Tracker tracker = Tracker.getInstance();
        this.crashHitsTimer.setFields(tracker.globalFields);

        //first submit the hits data to the portal
        try {
            final URL urlHits = new URL(configuration.getTrackerUrl());
            configuration.getLogger().debug("Submitting crash timer");
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
                configuration.getLogger().error("Error submitting crash timer: %s - %s", statusCodeHits, responseBody);
            }
            connectionHits.getHeaderField(0);
        } catch (Exception e) {
            configuration.getLogger().error(e, "Error submitting crash timer: %s", e.getMessage());
        } finally {
            if (connectionHits != null) {
                connectionHits.disconnect();
            }
        }
        configuration.getLogger().debug("Crash report timer successfully");

        // send crash data
        try {

            final String siteUrl = Uri.parse(configuration.getErrorReportingUrl())
                    .buildUpon()
                    .appendQueryParameter(Timer.FIELD_SITE_ID, configuration.getSiteId())
                    .appendQueryParameter(FIELD_ERROR_NAVIGATION_START, crashHitsTimer.getField(Timer.FIELD_NST))
                    .appendQueryParameter(Timer.FIELD_PAGE_NAME,
                            crashHitsTimer.getField(Timer.FIELD_PAGE_NAME, "Android Crash " + deviceName))
                    .appendQueryParameter(Timer.FIELD_TRAFFIC_SEGMENT_NAME,
                            crashHitsTimer.getField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, "Android Crash"))
                    .appendQueryParameter(Timer.FIELD_NATIVE_OS,
                            crashHitsTimer.getField(Timer.FIELD_NATIVE_OS, "Android"))
                    .appendQueryParameter(Timer.FIELD_DEVICE, crashHitsTimer.getField(Timer.FIELD_DEVICE, "Mobile"))
                    .appendQueryParameter(Timer.FIELD_BROWSER, Constants.BROWSER)
                    .appendQueryParameter(Timer.FIELD_BROWSER_VERSION,
                            crashHitsTimer.getField(Timer.FIELD_BROWSER_VERSION))
                    .appendQueryParameter(FIELD_ERROR_SESSION_ID, configuration.getSessionId())
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
            configuration.getLogger().debug("Crash Report URL: %s", siteUrl);
            final URL url = new URL(siteUrl);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            connection.setDoInput(false);
            final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(buildBase64EncodedJson(this.stackTrace, this.timeStamp, configuration.getApplicationName()));
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
                configuration.getLogger().error("Error submitting crash report: %s - %s", statusCode, responseBody);
            }
            connection.getHeaderField(0);
        } catch (Exception e) {
            configuration.getLogger().error(e, "Error submitting crash report: %s", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        configuration.getLogger().debug("Crash report submitted successfully");
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
        final String jsonData = crashDataArray.toString();
        configuration.getLogger().debug("Crash Report Data: " + jsonData);
        return Base64.encode(jsonData.getBytes("UTF-8"), Base64.DEFAULT);
    }

    /**
     * Builds a JSON string of the timer's fields
     *
     * @return a string of JSON representing the timer
     */
    private String buildJson() {
        final JSONObject data = new JSONObject(this.crashHitsTimer.getFields());
        final String jsonData = data.toString();
        configuration.getLogger().debug("Crash Report Data: " + jsonData);
        return jsonData;
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
