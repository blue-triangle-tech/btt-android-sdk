package com.bluetriangle.android.demo

import android.app.Application
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.init

class DemoApplication : Application() {
    private var tracker: Tracker? = null
    override fun onCreate() {
        super.onCreate()
        // d.btttag.com => 107.22.227.162
        //"http://107.22.227.162/btt.gif"
        //https://d.btttag.com/analytics.rcv
        //sdkdemo26621z
        //bluetriangledemo500z
        tracker = init(applicationContext)
        tracker!!.setSessionTrafficSegmentName("Demo Traffic Segment")
        //tracker.raiseTestException();
    }
}