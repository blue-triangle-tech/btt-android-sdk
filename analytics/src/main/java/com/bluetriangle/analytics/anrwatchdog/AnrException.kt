package com.bluetriangle.analytics.anrwatchdog

import android.os.Looper

class AnrException(val delay:Long): Exception("App not responding for last $delay ms") {

    override fun fillInStackTrace(): Throwable {
        stackTrace = Looper.getMainLooper().thread.stackTrace
        return this
    }

}