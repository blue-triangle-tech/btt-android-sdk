package com.bluetriangle.analytics

import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DEVICE_MODEL
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_NETWORK_STATE

data class ErrorNativeAppProperties(
    private var netState: String?=null,
    private var deviceModel:String?=null
) {

    fun add(deviceInfo: DeviceInfo) {
        deviceModel = deviceInfo.deviceModel
    }

    fun toMap(): Map<String, String?>  = hashMapOf<String, String?>().apply {
        netState?.let { this[FIELD_NETWORK_STATE] = netState }
        deviceModel?.let { this[FIELD_DEVICE_MODEL] = deviceModel }
    }
}
