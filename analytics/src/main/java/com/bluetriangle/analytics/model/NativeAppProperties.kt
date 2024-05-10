package com.bluetriangle.analytics.model

import android.os.Parcelable
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
internal data class NativeAppProperties(
    var loadTime: Long? = null,
    var fullTime: Long? = null,
    var maxMainThreadUsage: Long? = null,
    var screenType: ScreenType? = null,
    var numberOfCPUCores: Long? = null,
    var wifi: Long? = null,
    var cellular: Long? = null,
    var ethernet: Long? = null,
    var offline: Long? = null,
    var launchScreenName:String?=null
) : Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("loadTime", loadTime)
        obj.put("fullTime", fullTime)
        obj.put("maxMainThreadUsage", maxMainThreadUsage)
        obj.put("screenType", screenType?.value)
        obj.put("numberOfCPUCores", numberOfCPUCores)

        var max = Long.MIN_VALUE
        var maxField = ""

        val putAndCalculateMax:(Long?, String) -> Unit = { field, name ->
            if (field != null && field != 0L) {
                obj.put(name, field)
                if (field > max) {
                    max = field
                    maxField = name
                }
            }
        }

        putAndCalculateMax(wifi, "wifi")
        putAndCalculateMax(cellular, "cellular")
        putAndCalculateMax(ethernet, "ethernet")
        putAndCalculateMax(offline, "offline")

        if (maxField.isNotEmpty()) {
            obj.put(CapturedRequest.FIELD_NETWORK_STATE, maxField)
        }
        obj.put("launchScreenName", launchScreenName)

        return obj
    }
}