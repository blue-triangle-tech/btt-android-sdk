package com.bluetriangle.android.demo.kotlin

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluetriangle.android.demo.DemoApplication
import com.bluetriangle.android.demo.HomeActivity
import com.bluetriangle.android.demo.tests.ANRTest
import com.bluetriangle.android.demo.tests.ANRTestScenario
import com.bluetriangle.android.demo.tests.LaunchTestScenario
import kotlin.system.exitProcess


class TestListViewModel : ViewModel() {
    val anrTest = MutableLiveData(ANRTest.HeavyLoopTest)
    val anrTestScenario = MutableLiveData(ANRTestScenario.OnActivityCreate)
    val launchTestScenario = MutableLiveData(LaunchTestScenario.OnApplicationCreate)

    fun onTestChange(pos: Int) {
        anrTest.postValue(ANRTest.values()[pos])
    }

    fun onTestScenarioChange(pos: Int) {
        anrTestScenario.postValue(ANRTestScenario.values()[pos])
    }

    fun onLaunchTestScenarioChange(pos: Int) {
        launchTestScenario.value = LaunchTestScenario.values()[pos]
    }

    fun onRunLaunchScenarioClicked(view: View) {
        val launchScenario = launchTestScenario.value?:return

        DemoApplication.sharedPreferencesMgr.setInt("LaunchScenario", launchScenario.ordinal)
        Log.d("CheckAndLaunchTest", "Saving Launch Scenario: ${launchScenario.ordinal}")
        when (launchScenario) {
            LaunchTestScenario.OnApplicationCreate,
            LaunchTestScenario.OnActivityCreate -> {
//                val intent = Intent(view.context, HomeActivity::class.java)
//                val pendingIntent = PendingIntent.getActivity(
//                    view.context,
//                    123456,
//                    intent,
//                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//                (view.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
//                android.os.Process.killProcess(android.os.Process.myPid())
                triggerRestart(view.context as Activity)
            }
            LaunchTestScenario.OnActivityStart,
            LaunchTestScenario.OnActivityResume -> {
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    view.context.startActivity(this)
                }
            }
        }
    }

    fun triggerRestart(context: Activity) {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        context.finish()
        Runtime.getRuntime().exit(0)
    }
}