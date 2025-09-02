package com.bluetriangle.analytics.breadcrumbs

import androidx.core.net.toUri
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Timer
import org.json.JSONArray

internal class UserEventsCollection(
    private var siteId: String,
    private var nStart: String,
    private var pageName: String,
    private var pageType: String,
    private var trafficSegment: String,
    private var sessionId: String,
    private var browserVersion: String,
    private var device: String,
    userEvent: UserEvent
) {
    private val userEvents: MutableList<UserEvent> = mutableListOf(userEvent)

    @Synchronized
    fun add(userEvent: UserEvent) {
        userEvents.add(userEvent)
    }

    fun buildUrl(baseUrl: String): String {
        return baseUrl.toUri().buildUpon()
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

    @Synchronized
    fun buildCapturedRequestData(indentSpaces: Int): String {
        val requests = JSONArray(userEvents.map {
            it.payload
        })
        return requests.toString(indentSpaces)
    }

    override fun toString(): String {
        return "Captured Request Collection $nStart (${userEvents.size})"
    }
}