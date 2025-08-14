package com.bluetriangle.analytics.breadcrumbs

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Payload
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.caching.classifier.CacheType
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class UserEventsRunnable(
    private val configuration: BlueTriangleConfiguration,
    private val userEventsCollection: List<UserEventsCollection>
) : Runnable {
    override fun run() {
        try {
            userEventsCollection.forEach {
                submitUserEventsCollection(it)
            }
        } catch (e: Exception) {
            configuration.logger?.error("Error while submitting captured requests: ${e.message}")
        }
    }

    private fun submitUserEventsCollection(userEventsCollection: UserEventsCollection) {
        var connection: HttpsURLConnection? = null
        val payloadData =
            userEventsCollection.buildCapturedRequestData(if (configuration.isDebug) 2 else 0)
        var url = configuration.networkCaptureUrl
        try {
            url = userEventsCollection.buildUrl(configuration.networkCaptureUrl)
            configuration.logger?.debug("Submitting $userEventsCollection to $url")
            configuration.logger?.debug("$userEventsCollection payload: $payloadData")
            val requestUrl = URL(url)
            connection = requestUrl.openConnection() as HttpsURLConnection
            connection.requestMethod = Constants.METHOD_POST
            connection.setRequestProperty(
                Constants.HEADER_CONTENT_TYPE,
                Constants.CONTENT_TYPE_JSON
            )
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.userAgent)
            connection.doOutput = true
            DataOutputStream(connection.outputStream).use { it.write(Utils.b64encode(payloadData)) }
            val statusCode = connection.responseCode
            if (statusCode >= 300) {
                val responseBody =
                    BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                configuration.logger?.error("Server Error submitting $userEventsCollection: $statusCode - $responseBody")

                // If server error, cache the payload and try again later
                if (statusCode >= 500) {
                    cachePayload(url, payloadData)
                }
            } else {
                configuration.logger?.debug("$userEventsCollection submitted successfully")
            }
            connection.getHeaderField(0)
        } catch (e: Exception) {
            configuration.logger?.error(
                e,
                "Android Error submitting $userEventsCollection: ${e.message}"
            )
            cachePayload(url, payloadData)
        } finally {
            connection?.disconnect()
        }
    }

    /**
     * Cache the crash report to try and send again in the future
     * @param url URL to send to
     * @param payloadData payload data to send
     */
    private fun cachePayload(url: String, payloadData: String) {
        configuration.logger?.info("Caching network capture report")
        configuration.payloadCache?.save(
            Payload(
                url = url,
                data = payloadData,
                type = CacheType.Wcd,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}