package com.bluetriangle.analytics

import android.os.Build
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Utils.exceptionToStacktrace
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class BtCrashHandler(
    private val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun uncaughtException(t: Thread, e: Throwable) {
        val timeStamp = System.currentTimeMillis().toString()
        val mostRecentTimer = Tracker.instance?.getMostRecentTimer()
        configuration.logger?.debug("Most Recent Timer: $mostRecentTimer")
        val crashHitsTimer = Timer().startWithoutPerformanceMonitor()
        if(mostRecentTimer != null) {
            mostRecentTimer.generateNativeAppProperties()
            crashHitsTimer.nativeAppProperties = mostRecentTimer.nativeAppProperties
        }
        crashHitsTimer.nativeAppProperties.add(deviceInfoProvider.getDeviceInfo())
        crashHitsTimer.setError(true)

        val stacktrace = exceptionToStacktrace(null, e)
        try {
            sendToServer(crashHitsTimer, mostRecentTimer, stacktrace, timeStamp)
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
        defaultUEH.uncaughtException(t, e)
    }

    @Throws(InterruptedException::class)
    private fun sendToServer(crashHitsTimer:Timer, mostRecentTimer:Timer?, stacktrace: String, timeStamp: String) {
        val thread = Thread(CrashRunnable(
            configuration, stacktrace, timeStamp,
            crashHitsTimer, mostRecentTimer = mostRecentTimer,
            deviceInfoProvider = deviceInfoProvider
        ))
        thread.start()
        thread.join()
    }
}