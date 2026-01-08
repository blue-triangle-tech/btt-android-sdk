package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils
import com.bluetriangle.analytics.deviceinfo.IDeviceInfoProvider

internal class AnrManager(
    private val configuration: BlueTriangleConfiguration,
    private val deviceInfoProvider: IDeviceInfoProvider
) :
    AnrListener {

    private val detector: AnrDetector = RunnableAnrDetector(configuration.trackAnrIntervalSec)

    init {
        detector.addAnrListener("AnrManager", this)
    }

    fun start() {
        detector.startDetection()
    }

    fun stop() {
        detector.stopDetection()
    }

    override fun onAppNotResponding(error: AnrException) {
        configuration.logger?.debug("Anr Received: ${error.message}")

        val timeStamp = System.currentTimeMillis().toString()
        val mostRecentTimer = Tracker.instance?.getMostRecentTimer()
        val stacktrace = Utils.exceptionToStacktrace(null, error)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    stacktrace,
                    timeStamp,
                    Tracker.BTErrorType.ANRWarning,
                    mostRecentTimer,
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