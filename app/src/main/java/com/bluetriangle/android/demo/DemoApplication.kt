package com.bluetriangle.android.demo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.init
import com.bluetriangle.analytics.screenTracking.ScreenTrackMonitor

class DemoApplication : Application() {
    private var tracker: Tracker? = null
    lateinit var screenTrackMonitor: ScreenTrackMonitor // for showing screen logs from app inside LogFragment

    companion object {
        lateinit var tinyDB: TinyDB
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        tinyDB = TinyDB(applicationContext)

        val siteId = tinyDB.getString("BttSiteId")
        if (!siteId.isNullOrBlank())
            intTracker(siteId) //mobelux3271241z or bluetriangledemo500z
    }

    fun intTracker(siteId: String?) {
        if (siteId.isNullOrBlank()) return

        val configuration = BlueTriangleConfiguration()
        configuration.isTrackCrashesEnabled = true
        configuration.siteId = siteId
        configuration.isDebug = true
        configuration.networkSampleRate = 1.0
        configuration.isPerformanceMonitorEnabled = true
        tracker = init(this, configuration)

        tracker!!.setSessionTrafficSegmentName("Demo Traffic Segment")

        // for showing screen logs from app inside LogFragment
        screenTrackMonitor = ScreenTrackMonitor(this, configuration)
    }
}