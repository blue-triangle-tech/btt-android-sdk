package com.bluetriangle.analytics.screenTracking

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration

class ScreenTrackMonitor(application: Application, configuration: BlueTriangleConfiguration) :
    IScreenTrackCallback {
    init {
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(this))
    }

    val screenLogs = arrayListOf<ScreenTrackLog>()

    override fun onScreenLoad(id: String, className: String, startTime: Long) {
        val timeTaken = System.currentTimeMillis() - startTime
        screenLogs.add(ScreenTrackLog(className, startTime, timeTaken, true))
        Log.e("onScreenLoad", "$className loaded in $timeTaken ms")
    }

    override fun onScreenView(id: String, className: String, startTime: Long) {
        val timeTaken = System.currentTimeMillis() - startTime
        screenLogs.add(ScreenTrackLog(className, startTime, timeTaken, false))
        Log.e("onScreenView", "$className viewed for $timeTaken ms")
    }

    data class ScreenTrackLog(
        val screenName: String,
        val startTime: Long,
        val timeTaken: Long,
        val isLoadEvent: Boolean = true
    )
}