package com.bluetriangle.analytics.deviceinfo

import android.os.Build

class DeviceInfoProvider:IDeviceInfoProvider {
    override fun getDeviceInfo() = DeviceInfo(
        deviceModel = Build.MODEL
    )
}