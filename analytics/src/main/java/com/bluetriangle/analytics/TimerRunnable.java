package com.bluetriangle.analytics;

import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
        try {
            final URL url = new URL(configuration.getTrackerUrl());
            // Log.d("Tracker URL", String.format("Tracker URL: %s", this.trackerUrl));
            connection = (HttpsURLConnection) url.openConnection();
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
                configuration.getLogger().error("Server Error submitting %s: %s - %s", timer, statusCode, responseBody);
            }
            connection.getHeaderField(0);

        } catch (Exception e) {
            configuration.getLogger().error(e, "Android Error submitting %s: %s", timer, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        configuration.getLogger().debug("%s submitted successfully", timer);
    }

    /**
     * Builds a JSON string of the timer's fields
     *
     * @return a string of JSON representing the timer
     */
    private String buildJson() {
        final JSONObject data = new JSONObject(timer.getFields());
        //Log.d("crash data sent:", data.toString());
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
