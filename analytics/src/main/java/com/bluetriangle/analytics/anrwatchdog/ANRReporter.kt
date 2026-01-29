package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class ANRReporter(
    private val deviceInfoProvider: IDeviceInfoProvider
) {
    fun reportANR(timer: Timer?, ANRWarningException: ANRWarningException) {
        val tracker = Tracker.instance ?: return
        val configuration = tracker.configuration

        val timeStamp = ANRWarningException.timestamp.toString()
        val stacktrace = Utils.exceptionToStacktrace(null, ANRWarningException, true)

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