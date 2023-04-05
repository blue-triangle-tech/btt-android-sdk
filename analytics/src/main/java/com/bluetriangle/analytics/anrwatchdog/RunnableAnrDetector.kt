package com.bluetriangle.analytics.anrwatchdog

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bluetriangle.analytics.util.Constants.ANR_DEFAULT_INTERVAL
import com.bluetriangle.analytics.util.Constants.CHECK_INTERVAL
import java.util.concurrent.Executors

class RunnableAnrDetector: AnrDetector(), Runnable {

    private var executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun startDetection() {
        executor.execute(this)
    }

    override fun stopDetection() {
        executor.shutdown()
        executor = Executors.newSingleThreadExecutor()
    }

    private var dummyTask = Runnable { }
    private var postTime:Long = 0L
    private var isNotified = false

    override fun run() {
        while(!Thread.interrupted()) {
            postToMainThreadIfQueueEmpty()
            sleepForCheckInterval()
            checkAndNotifyAnr()
        }
    }

    private fun checkAndNotifyAnr() {
        val delay = System.currentTimeMillis() - postTime

        if(isAnrOccurred(delay)) {
            notifyIfNotAlreadyNotified(delay)
            return
        }
        isNotified = false
    }

    private fun notifyIfNotAlreadyNotified(delay:Long) {
        if(!isNotified) {
            isNotified = true
            Log.d("RunnableAnrDetector", "Notifying ANR")
            notifyListeners(AnrException(delay))
        }
    }

    private fun isAnrOccurred(delay:Long): Boolean {
        return handler.hasMessages(0) && delay >= ANR_DEFAULT_INTERVAL
    }

    private fun sleepForCheckInterval() {
        try {
            Thread.sleep(CHECK_INTERVAL)
        } catch (e: Exception) {
            Log.d("RunnableAnrDetector", "Exception while calling sleep on detector thread: ${e.message}")
        }
    }

    private fun postToMainThreadIfQueueEmpty() {
        if(!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }
}