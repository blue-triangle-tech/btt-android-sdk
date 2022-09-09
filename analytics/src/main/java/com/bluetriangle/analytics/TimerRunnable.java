package com.bluetriangle.analytics;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;

final class TimerRunnable implements Runnable {
    /**
     *  The tracker configuration
     */
    final BlueTriangleConfiguration configuration;

    /**
     * The timer to submit
     */
    final Timer timer;

    /**
     * Create a timer runnable to submit the given timer to the given URL
     *
     * @param configuration tracker configuration
     * @param timer      the timer to submit
     */
    TimerRunnable(@NonNull final BlueTriangleConfiguration configuration, @NonNull final Timer timer) {
        super();
        this.configuration = configuration;
        this.timer = timer;
    }

    @Override
    public void run() {
        HttpsURLConnection connection = null;
        final URL url;
        final String payloadData;

        try {
            url = new URL(configuration.getTrackerUrl());
        } catch (MalformedURLException e) {
            configuration.getLogger().error(e, "malformed timer URL: %s", configuration.getTrackerUrl());
            return;
        }

        try {
            payloadData = buildTimerData();
        } catch (UnsupportedEncodingException e) {
            configuration.getLogger().error(e, "Error building timer data: %s", timer);
            return;
        }

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);
            final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(Utils.b64encode(payloadData));
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
                configuration.getLogger().error("Server Error submitting %s: %s - %s", timer, statusCode, responseBody);

                // If server error, cache the payload and try again later
                if (statusCode >= 500) {
                    cachePayload(configuration.getTrackerUrl(), payloadData);
                }
            } else {
                configuration.getLogger().debug("%s submitted successfully", timer);

                // successfully submitted a timer, lets check if there are any cached timers that we can try and submit too
                final Payload nextCachedPayload = configuration.getPayloadCache().getNextCachedPayload();
                if (nextCachedPayload != null) {
                    Tracker.getInstance().submitPayload(nextCachedPayload);
                }
            }

            connection.getHeaderField(0);
        } catch (Exception e) {
            configuration.getLogger().error(e, "Android Error submitting %s: %s", timer, e.getMessage());
            cachePayload(configuration.getTrackerUrl(), payloadData);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Build the JSON data to send to the API
     *
     * @return JSON string
     * @throws UnsupportedEncodingException if UTF-8 encoding is not supported
     */
    private String buildTimerData() throws UnsupportedEncodingException {
        final JSONObject data = new JSONObject(timer.getFields());
        final String jsonData = data.toString();
        configuration.getLogger().debug("Timer Data: %s", jsonData);
        return jsonData;
    }

    /**
     * Cache the current timer to try and send again in the future
     * @param url URL to send to
     * @param payloadData payload data to send
     */
    private void cachePayload(final String url, final String payloadData) {
        configuration.getLogger().info("Caching timer %s", timer);
        configuration.getPayloadCache().cachePayload(new Payload(url, payloadData));
    }
}
