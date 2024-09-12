package com.bluetriangle.analytics.anrwatchdog

import android.os.Handler
import android.os.Looper
import com.bluetriangle.analytics.Constants.ANR_DEFAULT_INTERVAL
import com.bluetriangle.analytics.Constants.CHECK_INTERVAL
import com.bluetriangle.analytics.Tracker
import java.util.concurrent.Executors

/**
 * ANR Detector based on the Runnable approach. Once started it will continuously monitor the main thread
 * and notify the listeners of an ANR state.
 */
internal class RunnableAnrDetector(private val trackAnrIntervalSec: Int = ANR_DEFAULT_INTERVAL) :
    AnrDetector(), Runnable {

    /**
     * Executor instance that runs the current instance of Anr Detector. We use single thread executor as we only have
     * a single instance of Anr detector running on this executor.
     */
    private var executor = Executors.newSingleThreadExecutor()

    /**
     * Main thread handler. If we want to monitor if any other thread is being blocked then we can replace this
     * handler instance with that thread's handler instance.
     */
    private val handler = Handler(Looper.getMainLooper())

    private val logger = Tracker.instance?.configuration?.logger

    /**
     * Start detecting ANR. Once started it creates an infinite loop which only ends when stopDetection is called
     */
    override fun startDetection() {
        executor.execute(this)
    }

    /**
     * Stop detecting ANR.
     */
    override fun stopDetection() {
        executor.shutdown()
        executor = Executors.newSingleThreadExecutor()
    }

    /**
     * Empty instance of runnable to be posted on the message queue to check if it can be executed.
     */
    private var dummyTask = Runnable { }

    /**
     * The last time when a dummy runnable was posted on the handler. we use this to check the delay between posting
     * a runnable and it being executed.
     */
    private var postTime: Long = 0L

    /**
     * Maintains if ANR has been notified to the listeners. It will be reset everytime the thread comes back up from ANR.
     * so for every instance of ANR, the listeners will be notified only once.
     */
    private var isNotified = false

    override fun run() {
        try {
            while (!Thread.interrupted()) {
                postToMainThreadIfQueueEmpty()
                Thread.sleep(CHECK_INTERVAL)
                checkAndNotifyAnr()
            }
        } catch (e: InterruptedException) {
            logger?.error(
                "ANRDetector: Exception while calling sleep on detector thread: ${e.message}"
            )
        }
    }


    /**
     * It calculates the delay between the last runnable post and current time. if the delay is greater than ANR delay and
     * the message queue has not been cleared then it considers it as an ANR state and notifies the listeners.
     */
    private fun checkAndNotifyAnr() {
        val delay = System.currentTimeMillis() - postTime

        if (isAnrOccurred(delay)) {
            notifyIfNotAlreadyNotified(delay)
            return
        }
        isNotified = false
    }

    /**
     * If not already notified for the current ANR then it notifies the listeners of the ANR
     */
    private fun notifyIfNotAlreadyNotified(delay: Long) {
        if (!isNotified) {
            isNotified = true
            notifyListeners(AnrException(delay))
        }
    }

    private fun isAnrOccurred(delay: Long): Boolean {
        return handler.hasMessages(0) && delay >= (trackAnrIntervalSec * 1000L)
    }

    /**
     * Checks if there are any messages in the queue
     * and if there aren't any then it posts a new empty runnable to the queue
     */
    private fun postToMainThreadIfQueueEmpty() {
        if (!handler.hasMessages(0)) {
            postTime = System.currentTimeMillis()
            handler.postAtFrontOfQueue(dummyTask)
        }
    }
}