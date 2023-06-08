package com.bluetriangle.android.demo

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.Tracker.Companion.init
import com.bluetriangle.android.demo.tests.ANRTest
import com.bluetriangle.android.demo.tests.ANRTestFactory
import com.bluetriangle.android.demo.tests.ANRTestScenario

class DemoApplication : Application() {
    private var tracker: Tracker? = null

    companion object {
        lateinit var sharedPreferencesMgr: SharedPreferencesMgr
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferencesMgr = SharedPreferencesMgr(applicationContext)

        // d.btttag.com => 107.22.227.162
        //"http://107.22.227.162/btt.gif"
        //https://d.btttag.com/analytics.rcv
        //sdkdemo26621z
        //bluetriangledemo500z
        val configuration = BlueTriangleConfiguration()
        configuration.isTrackCrashesEnabled = true
        configuration.siteId = "mobelux3271241z"
        configuration.isDebug = true
        configuration.networkSampleRate = 1.0
        configuration.isPerformanceMonitorEnabled = true
        tracker = init(applicationContext, configuration)
        tracker!!.setSessionTrafficSegmentName("Demo Traffic Segment")
        //tracker.raiseTestException();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkANRTestOnAppCreate()
    }

    private fun checkANRTestOnAppCreate() {
        val anrTestScenarioId = sharedPreferencesMgr.getInt("ANRTestScenario")
        val anrTestId = sharedPreferencesMgr.getInt("ANRTest")
        sharedPreferencesMgr.remove("ANRTestScenario")
        sharedPreferencesMgr.remove("ANRTest")

        Log.e("ANRTestScenario", anrTestScenarioId.toString())
        Log.e("ANRTest", anrTestId.toString())

        val anrTestScenario = ANRTestScenario.values()[anrTestScenarioId]
        val anrTest = ANRTest.values()[anrTestId]
        if (anrTestScenario == ANRTestScenario.OnApplicationCreate && anrTest != ANRTest.Unknown) {
            ANRTestFactory.getANRTest(anrTest).run()
        }
    }
}