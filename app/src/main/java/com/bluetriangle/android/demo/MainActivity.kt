package com.bluetriangle.android.demo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker.Companion.instance
import com.bluetriangle.analytics.anrwatchdog.AnrException
import com.bluetriangle.analytics.anrwatchdog.AnrListener
import com.bluetriangle.analytics.anrwatchdog.AnrManager
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpInterceptor
import com.bluetriangle.android.demo.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.*

@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {
    private var timer: Timer? = null

    private var okHttpClient: OkHttpClient? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setTitle(R.string.main_title)

        updateButtonState()
        addButtonClickListeners()

        setUpAnrManager()

        okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(BlueTriangleOkHttpInterceptor(instance!!.configuration))
                .build()
    }

    private fun addButtonClickListeners() {
        binding.buttonStart.setOnClickListener(this::startButtonClicked)
        binding.buttonInteractive.setOnClickListener(this::interactiveButtonClicked)
        binding.buttonStop.setOnClickListener(this::stopButtonClicked)
        binding.buttonNext.setOnClickListener(this::nextButtonClicked)
        binding.buttonBackground.setOnClickListener(this::backgroundButtonClicked)
        binding.buttonCrash.setOnClickListener(this::crashButtonClicked)
        binding.buttonTrackCatchException.setOnClickListener(this::trackCatchExceptionButtonClicked)
        binding.buttonNetwork.setOnClickListener(this::captureNetworkRequests)
        binding.buttonAnr.setOnClickListener {
            startActivity(Intent(this, ANRTestActivity::class.java))
        }
    }

    private fun setUpAnrManager() {
        val anrManager = AnrManager()
        anrManager.start()
        anrManager.detector.addAnrListener(
            "UIThread",
            object : AnrListener {
                override fun onAppNotResponding(error: AnrException) {
                    showAnrNotification(error)
                }
            })
    }

    private fun showAnrNotification(error: AnrException) {
        val notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannelCompat.Builder("ANR", NotificationManagerCompat.IMPORTANCE_HIGH)
                    .setName("ANR Channel")
                    .build()
            notificationManager.createNotificationChannel(channel)
        }
//        val notification = NotificationCompat.Builder(this, "ANR")
//            .setContentTitle("ANR Detected")
//            .setContentText("Main thread is being blocked for the last " + error.delay + "ms")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//        notificationManager.notify(2, notification)
    }

    private fun updateButtonState() {
        binding.buttonStart.isEnabled = timer == null || !timer!!.isRunning() || timer!!.hasEnded()
        binding.buttonInteractive.isEnabled =
            timer != null && timer!!.isRunning() && !timer!!.isInteractive() && !timer!!.hasEnded()
        binding.buttonStop.isEnabled = timer != null && timer!!.isRunning() && !timer!!.hasEnded()
    }

    private fun startButtonClicked(view: View) {
        timer = Timer(MainActivity::class.java.simpleName, "Ä Traffic Šegment").start()
        updateButtonState()
    }

    private fun interactiveButtonClicked(view: View) {
        if (timer!!.isRunning()) {
            timer!!.interactive()
        }
        updateButtonState()
    }

    private fun stopButtonClicked(view: View) {
        if (timer!!.isRunning()) {
            timer!!.end().submit()
        }
        updateButtonState()
    }

    private fun nextButtonClicked(view: View) {
        val timer = Timer("Next Page", "Android Traffic").start()
        val intent = Intent(this, NextActivity::class.java)
        intent.putExtra(Timer.EXTRA_TIMER, timer)
        startActivity(intent)
    }

    private fun backgroundButtonClicked(view: View) {
        val backgroundTimer = Timer("Background Timer", "background traffic").start()

        lifecycleScope.launch {
            try {
                delay(500)
                backgroundTimer.interactive()
                delay(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            backgroundTimer.end().submit()
        }
    }

    private fun trackCatchExceptionButtonClicked(view: View) {
        try {
            instance!!.raiseTestException()
        } catch (e: Throwable) {
            instance!!.trackException("A test exception caught!", e)
        }
    }

    private fun crashButtonClicked(view: View) {
        instance!!.raiseTestException()
    }

    private fun captureNetworkRequests(view: View) {
        val timer = Timer("Test Network Capture", "Android Traffic").start()
        val imageRequest: Request =
            Request.Builder().url("https://www.httpbin.org/image/jpeg").build()
        okHttpClient!!.newCall(imageRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: " + call.request())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse: " + call.request())
            }
        })
        val jsonRequest: Request = Request.Builder().url("https://www.httpbin.org/json").build()
        okHttpClient!!.newCall(jsonRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: " + call.request())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse: " + call.request())
                timer.end().submit()
                val body: RequestBody = FormBody.Builder().add("test", "value").build()
                val postRequest: Request =
                    Request.Builder().url("https://httpbin.org/post").method("POST", body).build()
                okHttpClient!!.newCall(postRequest).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "onFailure: " + call.request())
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "onResponse: " + call.request())
                        Timer("Test Network Capture 2", "Android Traffic").start().end().submit()
                    }
                })
            }
        })
    }

    companion object {
        private const val TAG = "BlueTriangle"
    }
}