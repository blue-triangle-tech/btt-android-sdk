package com.bluetriangle.analytics.networkcapture

import android.net.Uri
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Timer
import org.json.JSONArray
import org.json.JSONObject

class CapturedRequestCollection(
    private var siteId: String,
    private var nStart: String,
    private var pageName: String,
    private var pageType: String,
    private var trafficSegment: String,
    private var sessionId: String,
    private var browserVersion: String,
    private var device: String,
    capturedRequest: CapturedRequest
) {
    private val capturedRequests: MutableList<CapturedRequest> = mutableListOf(capturedRequest)

    fun add(capturedRequest: CapturedRequest) {
        capturedRequests.add(capturedRequest)
    }

    fun buildUrl(baseUrl: String): String {
        return Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter(Timer.FIELD_SITE_ID, siteId)
            .appendQueryParameter(Timer.FIELD_NAVIGATION_START, nStart)
            .appendQueryParameter(Timer.FIELD_TRAFFIC_SEGMENT_NAME, trafficSegment)
            .appendQueryParameter(Timer.FIELD_LONG_SESSION_ID, sessionId)
            .appendQueryParameter(Timer.FIELD_PAGE_NAME, pageName)
            .appendQueryParameter(Timer.FIELD_CONTENT_GROUP_NAME, pageType)
            .appendQueryParameter(Timer.FIELD_WCDTT, "c")
            .appendQueryParameter(Timer.FIELD_NATIVE_OS, Constants.OS)
            .appendQueryParameter(Timer.FIELD_BROWSER, Constants.BROWSER)
            .appendQueryParameter(Timer.FIELD_BROWSER_VERSION, browserVersion)
            .appendQueryParameter(Timer.FIELD_DEVICE, device)
            .build().toString()
    }

    fun buildCapturedRequestData(indentSpaces: Int): String {
        val requests = JSONArray(capturedRequests.map { it.payload })
        return requests.toString(indentSpaces)
    }

    override fun toString(): String {
        return "Captured Request Collection $nStart (${capturedRequests.size})"
    }
}