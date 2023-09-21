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
import com.bluetriangle.android.demo.tests.HeavyLoopTest
import com.bluetriangle.android.demo.tests.LaunchTestScenario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DemoApplication : Application() {
    private var tracker: Tracker? = null

    companion object {
        lateinit var sharedPreferencesMgr: SharedPreferencesMgr
        lateinit var tinyDB: TinyDB

        fun checkLaunchTest(scenario: LaunchTestScenario) {
            val launchScenarioId = sharedPreferencesMgr.getInt("LaunchScenario", -1)
            Log.d("CheckAndLaunchTest", "LaunchScenarioId: $launchScenarioId")
            if(launchScenarioId == -1) return
            val launchScenario = LaunchTestScenario.values()[launchScenarioId]

            if(launchScenario == scenario) {
                sharedPreferencesMgr.remove("LaunchScenario")
                HeavyLoopTest(3L).run()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        tinyDB = TinyDB(applicationContext)

        sharedPreferencesMgr = SharedPreferencesMgr(applicationContext)

        initTracker("sdkdemo26621z")
        // d.btttag.com => 107.22.227.162
        //"http://107.22.227.162/btt.gif"
        //https://d.btttag.com/analytics.rcv
        //sdkdemo26621z
        //bluetriangledemo500z

        checkANRTestOnAppCreate()
        checkLaunchTest(LaunchTestScenario.OnApplicationCreate)
    }

    private fun initTracker(siteId: String?) {
        if (siteId.isNullOrBlank()) return

        val configuration = BlueTriangleConfiguration()
        configuration.isScreenTrackingEnabled = true
        configuration.isTrackCrashesEnabled = true
        configuration.isTrackAnrEnabled = true
        configuration.siteId = siteId
        configuration.isDebug = true
        configuration.networkSampleRate = 1.0
        configuration.isPerformanceMonitorEnabled = true
        configuration.isLaunchTimeEnabled = true
        configuration.sessionId = SimpleDateFormat("ddMMyyyykkmm", Locale.getDefault())
            .format(Calendar.getInstance().time)
        tracker = init(this, configuration)

        tracker!!.setSessionTrafficSegmentName("Demo Traffic Segment")
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