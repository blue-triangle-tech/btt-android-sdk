package com.bluetriangle.analytics.networkcapture

import android.os.Parcelable
import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class NetworkNativeAppProperties(
    var err: String?,
    var netState: String?,
    var deviceModel: String? = null
) : Parcelable {
    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("err", err)
        obj.put(CapturedRequest.FIELD_NETWORK_STATE, netState)
        obj.put(CapturedRequest.FIELD_DEVICE_MODEL, deviceModel)
        return obj
    }

    fun add(deviceInfo: DeviceInfo) {
        deviceModel = deviceInfo.deviceModel
    }
}
