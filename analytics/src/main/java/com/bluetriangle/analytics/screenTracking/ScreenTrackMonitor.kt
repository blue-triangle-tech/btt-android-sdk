package com.bluetriangle.analytics.screenTracking

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Timer

class ScreenTrackMonitor(application: Application, configuration: BlueTriangleConfiguration) :
    IScreenTrackCallback {
    init {
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(this))
    }

    val screenLogs = arrayListOf<ScreenTrackLog>()

    override fun onScreenLoad(id: String, className: String, timer: Timer) {
        val timeTaken = System.currentTimeMillis() - timer.start
        screenLogs.add(ScreenTrackLog(className, timer.start, timeTaken, true))
        Log.e("onScreenLoad", "$className loaded in $timeTaken ms")
        timer.end().submit()
    }

    override fun onScreenView(id: String, className: String, timer: Timer) {
        val timeTaken = System.currentTimeMillis() - timer.start
        screenLogs.add(ScreenTrackLog(className, timer.start, timeTaken, false))
        Log.e("onScreenView", "$className viewed for $timeTaken ms")
        timer.end().submit()
    }

    data class ScreenTrackLog(
        val screenName: String,
        val startTime: Long,
        val timeTaken: Long,
        val isLoadEvent: Boolean = true
    )
}