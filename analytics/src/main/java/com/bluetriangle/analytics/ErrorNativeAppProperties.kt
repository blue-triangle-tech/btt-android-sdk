package com.bluetriangle.analytics

import com.bluetriangle.analytics.Constants.APP_VERSION
import com.bluetriangle.analytics.Constants.SDK_VERSION
import com.bluetriangle.analytics.Timer.Companion.FIELD_NET_STATE_SOURCE
import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DEVICE_MODEL
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_NETWORK_STATE

data class ErrorNativeAppProperties(
    var netState: String? = null,
    private var deviceModel: String? = null,
    var netStateSource: String? = null
) {

    val appVersion: String? = Tracker.instance?.appVersion
    val sdkVersion: String = BuildConfig.SDK_VERSION

    fun add(deviceInfo: DeviceInfo) {
        deviceModel = deviceInfo.deviceModel
    }

    fun toMap(): Map<String, String?> = hashMapOf<String, String?>().apply {
        netState?.let { this[FIELD_NETWORK_STATE] = netState }
        deviceModel?.let { this[FIELD_DEVICE_MODEL] = deviceModel }
        if(!netStateSource.isNullOrEmpty()) {
            this[FIELD_NET_STATE_SOURCE] = netStateSource
        }
        appVersion?.let {
            this[SDK_VERSION] = sdkVersion
            this[APP_VERSION] = appVersion
        }
    }
}
