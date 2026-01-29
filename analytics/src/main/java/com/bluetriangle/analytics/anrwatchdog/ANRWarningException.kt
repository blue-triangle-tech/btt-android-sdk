package com.bluetriangle.analytics.anrwatchdog

import android.os.Looper

internal class ANRWarningException(anrInterval: Int) :
    Exception("App not responding for more than ${anrInterval * 1000L}ms") {
    val timestamp = System.currentTimeMillis()

    override fun fillInStackTrace(): Throwable {
        stackTrace = Looper.getMainLooper().thread.stackTrace
        return this
    }

}