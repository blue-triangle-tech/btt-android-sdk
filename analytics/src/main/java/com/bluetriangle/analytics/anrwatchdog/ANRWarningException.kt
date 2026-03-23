package com.bluetriangle.analytics.anrwatchdog

import android.os.Looper
import org.json.JSONArray

internal class ANRWarningException(anrInterval: Int, val breadcrumbs: JSONArray?) :
    Exception("App not responding for more than ${anrInterval * 1000L}ms") {
    val timestamp = System.currentTimeMillis()

    override fun fillInStackTrace(): Throwable {
        stackTrace = Looper.getMainLooper().thread.stackTrace
        return this
    }

}