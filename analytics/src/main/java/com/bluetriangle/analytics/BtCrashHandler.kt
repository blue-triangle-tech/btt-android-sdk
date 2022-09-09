package com.bluetriangle.analytics

import android.os.Build
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Utils.exceptionToStacktrace

internal class BtCrashHandler(private val configuration: BlueTriangleConfiguration) : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var crashHitsTimer: Timer? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun uncaughtException(t: Thread, e: Throwable) {
        val timeStamp = System.currentTimeMillis().toString()
        crashHitsTimer = Timer().startWithoutPerformanceMonitor()
        val stacktrace = exceptionToStacktrace(null, e)
        try {
            sendToServer(stacktrace, timeStamp)
        } catch (interruptedException: InterruptedException) {
            interruptedException.printStackTrace()
        }
        defaultUEH.uncaughtException(t, e)
    }

    @Throws(InterruptedException::class)
    private fun sendToServer(stacktrace: String, timeStamp: String) {
        val thread = Thread(CrashRunnable(configuration, stacktrace, timeStamp, crashHitsTimer!!))
        thread.start()
        thread.join()
    }
}