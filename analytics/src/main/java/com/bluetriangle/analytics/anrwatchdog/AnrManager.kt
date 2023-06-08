package com.bluetriangle.analytics.anrwatchdog

import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.CrashRunnable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Utils

class AnrManager(private val configuration: BlueTriangleConfiguration) : AnrListener {

    val detector: AnrDetector = RunnableAnrDetector(configuration.trackAnrIntervalSec)

    init {
        detector.addAnrListener("ANR", this)
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
        Log.d("AnrManager", "Anr Received: ${error.message}")

        val timeStamp = System.currentTimeMillis().toString()
        val crashHitsTimer: Timer = Timer().startWithoutPerformanceMonitor()

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