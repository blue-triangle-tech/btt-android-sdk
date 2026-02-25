package com.bluetriangle.analytics.breadcrumbs

import com.bluetriangle.analytics.utility.JsonMappable
import com.bluetriangle.analytics.utility.JsonWritable
import org.json.JSONObject

internal sealed class BreadcrumbEvent(
    val type: String,
    val timestamp: Long,
    val data: Data
) : JsonMappable {
    interface Data : JsonWritable

    override fun toJson() = JSONObject().apply {
        put("type", type)
        put("timestamp", timestamp)
        data.writeTo(this)
    }

    class Navigation(data: NavigationData) :
        BreadcrumbEvent("ui.navigation", System.currentTimeMillis(), data) {
        data class NavigationData(
            val from: String,
            val to: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("from", from)
                jsonObject.put("to", to)
            }
        }
    }

    class UiLifecycle(data: UiLifecycleData) :
        BreadcrumbEvent("ui.lifecycle", System.currentTimeMillis(), data) {
        data class UiLifecycleData(
            val className: String,
            val event: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("className", className)
                jsonObject.put("event", event)
            }
        }
    }

    class AppLifecycle(data: AppLifecycleData) :
        BreadcrumbEvent("app.lifecycle", System.currentTimeMillis(), data) {
        data class AppLifecycleData(
            val event: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("event", event)
            }
        }
    }

    class UserAction(data: UserActionData) :
        BreadcrumbEvent("user.action", System.currentTimeMillis(), data) {
        data class UserActionData(
            val action: String,
            val target: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("action", action)
                jsonObject.put("target", target)
            }
        }
    }

}