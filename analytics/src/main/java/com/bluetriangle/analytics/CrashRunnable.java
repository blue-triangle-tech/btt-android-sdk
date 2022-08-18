package com.bluetriangle.analytics;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
     * @param configuration  the tracker configuration
     * @param stackTrace     the error stack trace
     * @param timeStamp      time stamp when error occurred
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
        submitTimer();
        submitCrashReport();
    }

    private void submitTimer() {
        final String deviceName = Utils.getDeviceName();
        final Tracker tracker = Tracker.getInstance();

        crashHitsTimer.end();
        crashHitsTimer.setPageName("Android Crash " + deviceName);
        crashHitsTimer.setFields(tracker.globalFields);

        final TimerRunnable timerRunnable = new TimerRunnable(configuration, crashHitsTimer);
        timerRunnable.run();
    }

    private String buildCrashReportUrl() {
        final String deviceName = Utils.getDeviceName();
        final String crashReportUrl = Uri.parse(configuration.getErrorReportingUrl())
                .buildUpon()
                .appendQueryParameter(Timer.FIELD_SITE_ID, configuration.getSiteId())
                .appendQueryParameter(FIELD_ERROR_NAVIGATION_START, crashHitsTimer.getField(Timer.FIELD_NST))
                .appendQueryParameter(Timer.FIELD_PAGE_NAME, crashHitsTimer.getField(Timer.FIELD_PAGE_NAME, "Android Crash " + deviceName))
                .appendQueryParameter(Timer.FIELD_TRAFFIC_SEGMENT_NAME, crashHitsTimer.getField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, "Android Crash"))
                .appendQueryParameter(Timer.FIELD_NATIVE_OS, crashHitsTimer.getField(Timer.FIELD_NATIVE_OS, Constants.OS))
                .appendQueryParameter(Timer.FIELD_DEVICE, crashHitsTimer.getField(Timer.FIELD_DEVICE, "Mobile"))
                .appendQueryParameter(Timer.FIELD_BROWSER, Constants.BROWSER)
                .appendQueryParameter(Timer.FIELD_BROWSER_VERSION, crashHitsTimer.getField(Timer.FIELD_BROWSER_VERSION))
                .appendQueryParameter(FIELD_ERROR_SESSION_ID, configuration.getSessionId())
                .appendQueryParameter(Timer.FIELD_PAGE_TIME, crashHitsTimer.getField("pgTm"))
                .appendQueryParameter(Timer.FIELD_CONTENT_GROUP_NAME, crashHitsTimer.getField(Timer.FIELD_CONTENT_GROUP_NAME, deviceName))
                .appendQueryParameter(Timer.FIELD_AB_TEST_ID, crashHitsTimer.getField(Timer.FIELD_AB_TEST_ID, "Default"))
                .appendQueryParameter(Timer.FIELD_DATACENTER, crashHitsTimer.getField(Timer.FIELD_DATACENTER, "Default"))
                .appendQueryParameter(Timer.FIELD_CAMPAIGN_NAME, crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_NAME, ""))
                .appendQueryParameter(Timer.FIELD_CAMPAIGN_MEDIUM, crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_MEDIUM, Constants.OS))
                .appendQueryParameter(Timer.FIELD_CAMPAIGN_SOURCE, crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_SOURCE, "Crash"))
                .build().toString();
        return crashReportUrl;
    }

    private void submitCrashReport() {
        HttpsURLConnection connection = null;
        final String crashReportUrl = buildCrashReportUrl();
        configuration.getLogger().debug("Crash Report URL: %s", crashReportUrl);
        final String payloadData = buildCrashReportData();
        final URL url;
        try {
            url = new URL(crashReportUrl);
        } catch (MalformedURLException e) {
            configuration.getLogger().error(e, "Malformed crash report URL: %s", crashReportUrl);
            return;
        }

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.METHOD_POST);
            connection.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.getUserAgent());
            connection.setDoOutput(true);
            connection.setDoInput(false);
            final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(Utils.b64encode(payloadData));
            dataOutputStream.close();

            final int statusCode = connection.getResponseCode();
            if (statusCode >= 300) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                final StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
                final String responseBody = builder.toString();
                configuration.getLogger().error("Error submitting crash report: %s - %s", statusCode, responseBody);
            }
            // If server error, cache the payload and try again later
            if (statusCode >= 500) {
                cachePayload(crashReportUrl, payloadData);
            }
            connection.getHeaderField(0);
        } catch (Exception e) {
            configuration.getLogger().error(e, "Error submitting crash report: %s", e.getMessage());
            cachePayload(crashReportUrl, payloadData);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Cache the crash report to try and send again in the future
     * @param url URL to send to
     * @param payloadData payload data to send
     */
    private void cachePayload(final String url, final String payloadData) {
        configuration.getLogger().info("Caching crash report");
        configuration.getPayloadCache().cachePayload(new Payload(url, payloadData));
    }

    /**
     * build the JSON payload of crash data
     *
     * @return base 64 encoded JSON payload
     */
    private String buildCrashReportData() {
        final HashMap<String, String> crashReport = new HashMap<>();
        crashReport.put("msg", this.stackTrace);
        crashReport.put("eTp", "NativeAppCrash");
        crashReport.put("eCnt", "1");
        crashReport.put("url", configuration.getApplicationName());
        crashReport.put("line", "1");
        crashReport.put("col", "1");
        crashReport.put("time", this.timeStamp);

        final JSONObject jsonCrashReport = new JSONObject(crashReport);
        final JSONArray crashDataArray = new JSONArray(Collections.singletonList(jsonCrashReport));
        final String jsonData = crashDataArray.toString();
        configuration.getLogger().debug("Crash Report Data: " + jsonData);
        return jsonData;
    }
}
