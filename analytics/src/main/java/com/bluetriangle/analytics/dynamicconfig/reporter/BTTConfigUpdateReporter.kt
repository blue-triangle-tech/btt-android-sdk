/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.reporter

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class BTTConfigUpdateReporter(
    val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) : IBTTConfigUpdateReporter {

    companion object {
        private const val CONFIG_UPDATE_PAGE_NAME = "BTTConfigUpdate"
    }

    override fun reportSuccess() {
        val timer = Timer()
        timer.setPageName(CONFIG_UPDATE_PAGE_NAME)
        timer.setTrafficSegmentName(CONFIG_UPDATE_PAGE_NAME)
        timer.setContentGroupName(CONFIG_UPDATE_PAGE_NAME)
        timer.startWithoutPerformanceMonitor()
        timer.submit()
    }

    override fun reportError(error: BTTConfigFetchError) {
        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    "${Tracker.BTErrorType.BTTConfigUpdateError.errorName} : ${error.reason}",
                    System.currentTimeMillis().toString(),
                    Tracker.BTErrorType.BTTConfigUpdateError,
                    deviceInfoProvider = deviceInfoProvider
                )
            )
            thread.start()
            thread.join()
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }
}