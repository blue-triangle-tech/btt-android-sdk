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

    class SystemEvent(data: SystemEventData) :
        BreadcrumbEvent("system.event", System.currentTimeMillis(), data) {
        data class SystemEventData(
            val eventType: String,
            val event: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("eventType", eventType)
                jsonObject.put("event", event)
            }
        }
    }

    class AppLifecycle(data: AppLifecycleData, timestamp: Long = System.currentTimeMillis()) :
        BreadcrumbEvent("app.lifecycle", timestamp, data) {
        data class AppLifecycleData(
            val event: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("event", event)
            }
        }
    }

    class NetworkRequest(data: NetworkRequestData) :
        BreadcrumbEvent("network.request", System.currentTimeMillis(), data) {
        data class NetworkRequestData(
            val url: String,
            val statusCode: Int
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("url", url)
                jsonObject.put("statusCode", statusCode.toString())
            }
        }
    }

    class NetworkState(data: NetworkStateData) :
        BreadcrumbEvent("network.state", System.currentTimeMillis(), data) {
        data class NetworkStateData(
            val state: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("state", state)
            }
        }
    }

    class UserEvent(data: UserEventData) :
        BreadcrumbEvent("user.event", System.currentTimeMillis(), data) {
        data class UserEventData(
            val action: String,
            val targetClass: String?,
            val targetId: String?,
            val x: Float,
            val y: Float
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("action", action)
                targetClass?.let {
                    jsonObject.put("targetClass", targetClass)
                }
                targetId?.let {
                    jsonObject.put("targetId", targetId)
                }
                jsonObject.put("x", x.toString())
                jsonObject.put("y", y.toString())
            }
        }
    }

    class AppInstall(data: AppInstallData) :
        BreadcrumbEvent("app.install", System.currentTimeMillis(), data) {
        data class AppInstallData(
            val version: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("version", version)
            }
        }
    }

    class AppUpdate(data: AppUpdateData) :
        BreadcrumbEvent("app.update", System.currentTimeMillis(), data) {
        data class AppUpdateData(
            val from: String,
            val to: String
        ) : Data {
            override fun writeTo(jsonObject: JSONObject) {
                jsonObject.put("from", from)
                jsonObject.put("to", to)
            }
        }
    }

}