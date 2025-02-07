package com.bluetriangle.analytics.deviceinfo

import android.os.Build

object DeviceInfoProvider:IDeviceInfoProvider {
    override fun getDeviceInfo() = DeviceInfo(
        deviceModel = Build.MODEL
    )
}