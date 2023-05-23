package com.bluetriangle.android.demo.kotlin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.DemoApplication
import com.bluetriangle.android.demo.HomeActivity
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityAnrTestBinding
import com.bluetriangle.android.demo.tests.ANRTest
import com.bluetriangle.android.demo.tests.ANRTestFactory
import com.bluetriangle.android.demo.tests.ANRTestScenario
import kotlin.system.exitProcess

class ANRTestActivity : AppCompatActivity() {
    private var anrTest = ANRTest.Unknown
    private var anrTestScenario = ANRTestScenario.Unknown
    private var receiver: MyReceiver? = null

    companion object {
        const val TestScenario = "TestScenario"
        const val Test = "Test"
        const val BroadCastName = "com.example.Broadcast"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAnrTestBinding>(
            this,
            R.layout.activity_anr_test
        )
        setTitle(R.string.anr_tests)
        binding.lifecycleOwner = this

        anrTest = (intent.extras?.getSerializable(Test) as ANRTest?) ?: ANRTest.Unknown
        anrTestScenario = (intent.extras?.getSerializable(TestScenario) as ANRTestScenario?)
            ?: ANRTestScenario.Unknown

        if (anrTest == ANRTest.Unknown || anrTestScenario == ANRTestScenario.Unknown) {
            binding.adapter = ANRTestAdapter(ANRTestFactory.getANRTests(), this)
        } else if (anrTestScenario == ANRTestScenario.OnActivityCreate) {
            ANRTestFactory.getANRTest(anrTest).run()
        } else if (anrTestScenario == ANRTestScenario.OnBroadCastReceived) {
            val filter = IntentFilter()
            filter.addAction(BroadCastName)
            receiver = MyReceiver()
            registerReceiver(receiver, filter)
        }

        val timerStatus = MutableLiveData(false)
        var timer: Timer? = null

        binding.status = timerStatus
        binding.startStopButton.setOnClickListener {
            if (timerStatus.value == true) {
                timer?.end()?.submit()
                timerStatus.value = false
            } else {
                timer = Timer()
                timer?.start()
                timerStatus.value = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (anrTestScenario != ANRTestScenario.Unknown && anrTest != ANRTest.Unknown) {
            if (anrTestScenario == ANRTestScenario.OnActivityResume)
                ANRTestFactory.getANRTest(anrTest).run()

            if (anrTestScenario == ANRTestScenario.OnBroadCastReceived) {
                Handler(Looper.getMainLooper()).postDelayed({
                    sendBroadcast(Intent(BroadCastName))
                }, 2000)
            }

            if (anrTestScenario == ANRTestScenario.OnApplicationCreate) {
                DemoApplication.sharedPreferencesMgr.setInt(
                    "ANRTestScenario",
                    anrTestScenario.ordinal
                )
                DemoApplication.sharedPreferencesMgr.setInt("ANRTest", anrTest.ordinal)
                restartApp(applicationContext)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let { unregisterReceiver(it) }
    }

    private fun restartApp(context: Context) {
        val intent = Intent(context, HomeActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(
            context,
            mPendingIntentId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC, System.currentTimeMillis() + 100] = mPendingIntent
        //Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            ANRTestFactory.getANRTest(anrTest).run()
        }
    }
}