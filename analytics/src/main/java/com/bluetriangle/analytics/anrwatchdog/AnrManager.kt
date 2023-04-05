package com.bluetriangle.analytics.anrwatchdog

import android.util.Log

class AnrManager : AnrListener {

    val detector:AnrDetector = RunnableAnrDetector()

    init {
        detector.addAnrListener("ANR", this)
    }
    fun start() {
        detector.startDetection()
    }

    fun stop() {
        detector.stopDetection()
    }

    override fun onAppNotResponding(error: AnrException) {
        Log.d("AnrManager", "Anr Received: ${error.message}")
        error.printStackTrace()
    }
}