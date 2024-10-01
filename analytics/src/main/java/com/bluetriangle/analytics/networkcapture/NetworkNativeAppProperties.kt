package com.bluetriangle.analytics.networkcapture

import android.os.Parcelable
import com.bluetriangle.analytics.Timer.Companion.FIELD_NET_STATE_SOURCE
import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class NetworkNativeAppProperties(
    var err: String?,
    var netState: String? = null,
    var deviceModel: String? = null,
    var netStateSource: String? = null
) : Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("err", err)
        obj.put(CapturedRequest.FIELD_NETWORK_STATE, netState)
        obj.put(CapturedRequest.FIELD_DEVICE_MODEL, deviceModel)
        if(!netStateSource.isNullOrEmpty()) {
            obj.put(FIELD_NET_STATE_SOURCE, netStateSource)
        }
        return obj
    }

    fun add(deviceInfo: DeviceInfo) {
        deviceModel = deviceInfo.deviceModel
    }
}
