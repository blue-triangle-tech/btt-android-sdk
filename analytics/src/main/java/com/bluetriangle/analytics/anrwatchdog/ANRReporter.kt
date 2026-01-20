package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class ANRReporter(
    private val deviceInfoProvider: IDeviceInfoProvider
) {
    fun reportANR(timer: Timer?, anrException: AnrException) {
        val tracker = Tracker.instance ?: return
        val configuration = tracker.configuration

        val timeStamp = anrException.timestamp.toString()
        val stacktrace = Utils.exceptionToStacktrace(null, anrException, true)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    stacktrace,
                    timeStamp,
                    Tracker.BTErrorType.ANRWarning,
                    timer,
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