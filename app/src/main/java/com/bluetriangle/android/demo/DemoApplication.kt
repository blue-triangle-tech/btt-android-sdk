package com.bluetriangle.android.demo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.init
import com.bluetriangle.analytics.screenTracking.ScreenTrackMonitor

class DemoApplication : Application() {
    private var tracker: Tracker? = null
    lateinit var scrTrack: ScreenTrackMonitor // for showing screen logs from app inside LogFragment

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // for showing screen logs from app inside LogFragment
        scrTrack = ScreenTrackMonitor(this, configuration)
    }
}