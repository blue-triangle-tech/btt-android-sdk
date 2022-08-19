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
    private val capturedRequests: MutableList<CapturedRequest> = ArrayList()

    init {
        add(capturedRequest)
    }

    fun add(capturedRequest: CapturedRequest) {
        capturedRequests.add(capturedRequest)
    }

    val queryParameters: Map<String, String>
        get() {
            val parameters = HashMap<String, String>(12)
            parameters[Timer.FIELD_SITE_ID] = siteId
            parameters[Timer.FIELD_NAVIGATION_START] = nStart
            parameters[Timer.FIELD_TRAFFIC_SEGMENT_NAME] = trafficSegment
            parameters[Timer.FIELD_LONG_SESSION_ID] = sessionId
            parameters[Timer.FIELD_PAGE_NAME] = pageName
            parameters[Timer.FIELD_CONTENT_GROUP_NAME] = pageType
            parameters[Timer.FIELD_WCDTT] = "c"
            parameters[Timer.FIELD_NATIVE_OS] = Constants.OS
            parameters[Timer.FIELD_BROWSER] = Constants.BROWSER
            parameters[Timer.FIELD_BROWSER_VERSION] = browserVersion
            parameters[Timer.FIELD_DEVICE] = device
            return parameters
        }

    fun buildCapturedRequestData(): String {
        val requests = JSONArray()
        for (capturedRequest in capturedRequests) {
            requests.put(JSONObject(capturedRequest.payload))
        }
        return requests.toString()
    }
}