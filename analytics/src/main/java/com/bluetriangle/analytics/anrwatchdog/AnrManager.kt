package com.bluetriangle.analytics.anrwatchdog

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils

internal class AnrManager constructor(private val configuration: BlueTriangleConfiguration) :
    AnrListener {

    private val detector: AnrDetector = RunnableAnrDetector(configuration.trackAnrIntervalSec)

    init {
        detector.addAnrListener("AnrManager", this)
    }

    fun start() {
        if (configuration.isTrackAnrEnabled)
            detector.startDetection()
    }

    fun stop() {
        if (configuration.isTrackAnrEnabled)
            detector.stopDetection()
    }

    override fun onAppNotResponding(error: AnrException) {
        configuration.logger?.debug("Anr Received: ${error.message}")

        val timeStamp = System.currentTimeMillis().toString()
        val mostRecentTimer = Tracker.instance?.getMostRecentTimer()
        val crashHitsTimer: Timer = Timer().startWithoutPerformanceMonitor()

        crashHitsTimer.setPageName(mostRecentTimer?.getField(FIELD_PAGE_NAME)?:Tracker.BTErrorType.ANRWarning.value)
        if(mostRecentTimer != null) {
            mostRecentTimer.generateNativeAppProperties()
            crashHitsTimer.nativeAppProperties = mostRecentTimer.nativeAppProperties
        }
        crashHitsTimer.setError(true)
        val stacktrace = Utils.exceptionToStacktrace(null, error)

        try {
            val thread = Thread(
                CrashRunnable(
                    configuration,
                    stacktrace,
                    timeStamp,
                    crashHitsTimer,
                    Tracker.BTErrorType.ANRWarning
                )
            )
            thread.start()
            thread.join()
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
    }
}