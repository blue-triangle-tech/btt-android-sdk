package com.bluetriangle.analytics.screenTracking

import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration

class ScreenTrackMonitor(application: Application, configuration: BlueTriangleConfiguration) :
    IScreenTrackCallback {
    init {
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks(this))
    }

    override fun onScreenLoad(id: String, className: String, timeTaken: Long) {
        Log.e("onScreenLoad", "$className loaded in $timeTaken ms")
    }

    override fun onScreenView(id: String, className: String, timeTaken: Long) {
        Log.e("onScreenView", "$className viewed for $timeTaken ms")
    }
}