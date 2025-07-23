package com.bluetriangle.analytics.screenTracking.grouping

import android.net.Uri
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DURATION
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_ENTRY_TYPE
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_FILE
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_START_TIME
import org.json.JSONObject

internal class GroupedDataCollection(
    private var siteId: String,
    private var nStart: String,
    private var pageName: String,
    private var pageType: String,
    private var trafficSegment: String,
    private var sessionId: String,
    private var browserVersion: String,
    private var device: String,
    private var children: List<BTTChildView>
) {
    fun buildChildViewsData(): List<JSONObject> {
        return children.map {
            JSONObject().apply {
                put(FIELD_FILE, it.className)
                put(FIELD_DURATION, it.pageTime)
                put(FIELD_START_TIME, it.startTime)
                put(FIELD_ENTRY_TYPE, BTTChildView.ENTRY_TYPE)
                put(CapturedRequest.FIELD_END_TIME, it.endTime)
                put(CapturedRequest.FIELD_URL, it.pageName)
                put(Timer.FIELD_NATIVE_APP, it.nativeAppProperties.toJSONObject())
            }
        }
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
}