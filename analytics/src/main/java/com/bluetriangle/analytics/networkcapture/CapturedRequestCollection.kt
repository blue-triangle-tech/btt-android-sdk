package com.bluetriangle.analytics.networkcapture

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

    val queryParameters: Map<String, String>
        get() {
            return mapOf(
                Timer.FIELD_SITE_ID to siteId,
                Timer.FIELD_NAVIGATION_START to nStart,
                Timer.FIELD_TRAFFIC_SEGMENT_NAME to trafficSegment,
                Timer.FIELD_LONG_SESSION_ID to sessionId,
                Timer.FIELD_PAGE_NAME to pageName,
                Timer.FIELD_CONTENT_GROUP_NAME to pageType,
                Timer.FIELD_WCDTT to "c",
                Timer.FIELD_NATIVE_OS to Constants.OS,
                Timer.FIELD_BROWSER to Constants.BROWSER,
                Timer.FIELD_BROWSER_VERSION to browserVersion,
                Timer.FIELD_DEVICE to device,
            )
        }

    fun buildCapturedRequestData(): String {
        val requests = JSONArray(capturedRequests.map { JSONObject(it.payload) })
        return requests.toString()
    }
}