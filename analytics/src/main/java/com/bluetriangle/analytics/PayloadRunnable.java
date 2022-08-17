package com.bluetriangle.analytics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;

public class PayloadRunnable implements Runnable {
    /**
     * The tracker configuration
     */
    @NonNull final BlueTriangleConfiguration configuration;

    /**
     * The payload to submit
     */
    @NonNull final Payload payload;

    /**
     * Create a timer runnable to submit the given timer to the given URL
     *
     * @param configuration tracker configuration
     * @param payload       the payload to submit
     */
    PayloadRunnable(@NonNull final BlueTriangleConfiguration configuration, @NonNull final Payload payload) {
        super();
        this.configuration = configuration;
        this.payload = payload;
    }

    @Override
    public void run() {
        HttpsURLConnection connection = null;
        final URL url;

        try {
            url = new URL(payload.url);
        } catch (MalformedURLException e) {
            configuration.getLogger().error(e, "malformed payload URL: %s", payload.url);
            return;
        }

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(Constants.METHOD_POST);
            connection.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.getUserAgent());
            connection.setDoOutput(true);
            final DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.write(Utils.b64encode(payload.data));
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
                configuration.getLogger().error("HTTP Error %s submitting payload %s: %s", statusCode, payload.id, responseBody);
                // If server error, cache the payload and try again later
                if (statusCode >= 500) {
                    cachePayload();
                }
            } else {
                configuration.getLogger().debug("Cached payload %s submitted successfully", payload.id);

                // successfully submitted a timer, lets check if there are any cached timers that we can try and submit too
                final Payload nextCachedPayload = configuration.getPayloadCache().getNextCachedPayload();
                if (nextCachedPayload != null) {
                    Tracker.getInstance().submitPayload(nextCachedPayload);
                }
            }

            connection.getHeaderField(0);
        } catch (Exception e) {
            configuration.getLogger().error(e, "Error submitting payload %s", payload.id);
            cachePayload();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Cache the payload to try again in the future
     */
    private void cachePayload() {
        configuration.getLogger().info("Caching crash report");
        configuration.getPayloadCache().cachePayload(payload);
    }
}
