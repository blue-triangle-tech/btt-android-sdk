package com.bluetriangle.android.demo

import android.app.Application
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.init

class DemoApplication : Application() {
    private var tracker: Tracker? = null
    override fun onCreate() {
        super.onCreate()
        val configuration = BlueTriangleConfiguration()
        configuration.isTrackCrashesEnabled = true
        configuration.siteId = "mobelux3271241z"
        configuration.isDebug = true
        configuration.networkSampleRate = 1.0
        configuration.isPerformanceMonitorEnabled = true
        tracker = init(this, configuration)

        tracker!!.setSessionTrafficSegmentName("Demo Traffic Segment")
    }
}