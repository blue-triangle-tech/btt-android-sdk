package com.bluetriangle.analytics

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Create a timer runnable to submit the given timer to the given URL
 *
 * @param configuration tracker configuration
 * @param payload       the payload to submit
 */
internal class PayloadRunnable(private val configuration: BlueTriangleConfiguration, val payload: Payload) : Runnable {
    override fun run() {
        var connection: HttpsURLConnection? = null
        try {
            val url = URL(payload.url)
            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = Constants.METHOD_POST
            connection.setRequestProperty(Constants.HEADER_CONTENT_TYPE, Constants.CONTENT_TYPE_JSON)
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.userAgent)
            connection.doOutput = true
            DataOutputStream(connection.outputStream).use { it.write(Utils.b64encode(payload.data)) }
            
            val statusCode = connection.responseCode
            if (statusCode >= 300) {
                val responseBody = BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                configuration.logger?.error("HTTP Error $statusCode submitting payload $payload : $responseBody")
                // If server error, cache the payload and try again later
                if (statusCode >= 500) {
                    cachePayload()
                }
            } else {
                configuration.logger?.debug("Cached payload $payload submitted successfully")

                // successfully submitted a timer, lets check if there are any cached timers that we can try and submit too
                val nextCachedPayload = configuration.payloadCache?.pickNext()
                if (nextCachedPayload != null) {
                    Tracker.instance?.submitPayload(nextCachedPayload)
                }
            }
            connection.getHeaderField(0)
        } catch (e: Exception) {
            configuration.logger?.error(e, "Error submitting payload ${payload.id}")
            cachePayload()
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Cache the payload to try again in the future
     */
    private fun cachePayload() {
        if (payload.payloadAttempts >= configuration.maxAttempts) {
            configuration.logger?.warn("Payload ${payload.id} has exceeded max attempts ${payload.payloadAttempts}")
            return
        }
        configuration.logger?.info("Caching crash report")
        configuration.payloadCache?.save(payload.copy(payloadAttempts = payload.payloadAttempts + 1))
    }
}