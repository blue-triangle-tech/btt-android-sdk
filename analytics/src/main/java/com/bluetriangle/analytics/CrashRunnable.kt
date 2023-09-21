package com.bluetriangle.analytics

import android.net.Uri
import com.bluetriangle.analytics.Constants.TIMER_MIN_PGTM
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Create a timer runnable to submit the given timer to the given URL
 *
 * @param configuration  the tracker configuration
 * @param stackTrace     the error stack trace
 * @param timeStamp      time stamp when error occurred
 * @param crashHitsTimer crash hit timer
 */
internal class CrashRunnable(
    /**
     * Tracker configuration
     */
    private val configuration: BlueTriangleConfiguration,
    /**
     * This is the crash report
     */
    private val stackTrace: String,
    /**
     * This is the epoch when the error happened
     */
    private val timeStamp: String,
    /**
     * Timer to track crash reporting
     */
    private val crashHitsTimer: Timer,

    private val errorType: Tracker.BTErrorType = Tracker.BTErrorType.NativeAppCrash,

    private val mostRecentTimer: Timer? = null,
) : Runnable {
    override fun run() {
        submitTimer()
        submitCrashReport()
    }

    private fun submitTimer() {
        val tracker = Tracker.instance
        crashHitsTimer.pageTimeCalculator = {
            TIMER_MIN_PGTM
        }
        crashHitsTimer.end()
        crashHitsTimer.setField(Timer.FIELD_EXCLUDED, "20")
        if(errorType == Tracker.BTErrorType.NativeAppCrash) {
            crashHitsTimer.setPageName(
                mostRecentTimer?.getField(Timer.FIELD_PAGE_NAME) ?: Constants.CRASH_PAGE_NAME
            )
        }
        crashHitsTimer.setFields(tracker?.globalFields?.toMap() ?: emptyMap())
        val timerRunnable = TimerRunnable(configuration, crashHitsTimer)
        timerRunnable.run()
    }

    private fun buildCrashReportUrl(): String {
        val deviceName = Utils.deviceName
        val pageName = mostRecentTimer?.getField(Timer.FIELD_PAGE_NAME)
            ?: Constants.CRASH_PAGE_NAME
        return Uri.parse(configuration.errorReportingUrl)
            .buildUpon()
            .appendQueryParameter(Timer.FIELD_SITE_ID, configuration.siteId)
            .appendQueryParameter(
                Timer.FIELD_NAVIGATION_START,
                crashHitsTimer.getField(Timer.FIELD_NST)
            )
            .appendQueryParameter(
                Timer.FIELD_PAGE_NAME,
                crashHitsTimer.getField(Timer.FIELD_PAGE_NAME, pageName)
            )
            .appendQueryParameter(
                Timer.FIELD_TRAFFIC_SEGMENT_NAME,
                crashHitsTimer.getField(Timer.FIELD_TRAFFIC_SEGMENT_NAME, pageName)
            )
            .appendQueryParameter(
                Timer.FIELD_NATIVE_OS,
                crashHitsTimer.getField(Timer.FIELD_NATIVE_OS, Constants.OS)
            )
            .appendQueryParameter(
                Timer.FIELD_DEVICE,
                crashHitsTimer.getField(Timer.FIELD_DEVICE, Constants.DEVICE_MOBILE)
            )
            .appendQueryParameter(Timer.FIELD_BROWSER, Constants.BROWSER)
            .appendQueryParameter(
                Timer.FIELD_BROWSER_VERSION,
                crashHitsTimer.getField(Timer.FIELD_BROWSER_VERSION)
            )
            .appendQueryParameter(Timer.FIELD_LONG_SESSION_ID, configuration.sessionId)
            .appendQueryParameter(Timer.FIELD_PAGE_TIME, crashHitsTimer.getField("pgTm"))
            .appendQueryParameter(
                Timer.FIELD_CONTENT_GROUP_NAME,
                crashHitsTimer.getField(Timer.FIELD_CONTENT_GROUP_NAME, deviceName)
            )
            .appendQueryParameter(
                Timer.FIELD_AB_TEST_ID,
                crashHitsTimer.getField(Timer.FIELD_AB_TEST_ID, "Default")
            )
            .appendQueryParameter(
                Timer.FIELD_DATACENTER,
                crashHitsTimer.getField(Timer.FIELD_DATACENTER, "Default")
            )
            .appendQueryParameter(
                Timer.FIELD_CAMPAIGN_NAME,
                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_NAME, "")
            )
            .appendQueryParameter(
                Timer.FIELD_CAMPAIGN_MEDIUM,
                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_MEDIUM, Constants.OS)
            )
            .appendQueryParameter(
                Timer.FIELD_CAMPAIGN_SOURCE,
                crashHitsTimer.getField(Timer.FIELD_CAMPAIGN_SOURCE, "Crash")
            )
            .build().toString()
    }

    private fun submitCrashReport() {
        var connection: HttpsURLConnection? = null
        val crashReportUrl = buildCrashReportUrl()
        configuration.logger?.debug("Crash Report URL: $crashReportUrl")
        val payloadData = buildCrashReportData()
        try {
            val url = URL(crashReportUrl)
            connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = Constants.METHOD_POST
            connection.setRequestProperty(
                Constants.HEADER_CONTENT_TYPE,
                Constants.CONTENT_TYPE_JSON
            )
            connection.setRequestProperty(Constants.HEADER_USER_AGENT, configuration.userAgent)
            connection.doOutput = true
            connection.doInput = false
            DataOutputStream(connection.outputStream).use { it.write(Utils.b64encode(payloadData)) }
            val statusCode = connection.responseCode
            if (statusCode >= 300) {
                val responseBody =
                    BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
                configuration.logger?.error("Error submitting crash report: $statusCode - $responseBody")
            }
            // If server error, cache the payload and try again later
            if (statusCode >= 500) {
                cachePayload(crashReportUrl, payloadData)
            }
            connection.getHeaderField(0)
        } catch (e: Exception) {
            configuration.logger?.error(e, "Error submitting crash report: ${e.message}")
            cachePayload(crashReportUrl, payloadData)
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
        if (errorType == Tracker.BTErrorType.ANRWarning) return
        configuration.logger?.info("Caching crash report")
        configuration.payloadCache?.cachePayload(Payload(url = url, data = payloadData))
    }

    /**
     * build the JSON payload of crash data
     *
     * @return base 64 encoded JSON payload
     */
    private fun buildCrashReportData(): String {
        val crashReport = mapOf(
            "msg" to stackTrace,
            "eTp" to errorType.value,
            "eCnt" to "1",
            "url" to configuration.applicationName,
            "line" to "1",
            "col" to "1",
            "time" to timeStamp,
        )

        val crashDataArray = JSONArray(listOf(JSONObject(crashReport)))
        val jsonData = crashDataArray.toString(if (configuration.isDebug) 2 else 0)
        configuration.logger?.debug("Crash Report Data: $jsonData")
        return jsonData
    }
}