package com.bluetriangle.analytics

import android.os.Process
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.TrackerExecutor
import com.bluetriangle.analytics.TrackerExecutor.TrackerThreadFactory
import com.bluetriangle.analytics.TrackerExecutor.TrackerThread
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * A thread pool executor for queueing and submitting timers
 */
internal class TrackerExecutor(private val configuration: BlueTriangleConfiguration) : ThreadPoolExecutor(
    DEFAULT_POOL_SIZE,
    MAX_POOL_SIZE,
    KEEP_ALIVE_TIME_MS,
    TimeUnit.MILLISECONDS,
    LinkedBlockingQueue(),
    TrackerThreadFactory()
) {
    /**
     * Builds threads with the given runnable
     */
    private class TrackerThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return TrackerThread(runnable)
        }
    }

    /**
     * A custom thread which sets priority to background and generates a unique name
     */
    private class TrackerThread internal constructor(runnable: Runnable?) :
        Thread(runnable, THREAD_NAME_PREFIX + SEQUENCE_GENERATOR.getAndIncrement()) {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            super.run()
        }

        companion object {
            private val SEQUENCE_GENERATOR = AtomicInteger(1)
        }
    }

    companion object {
        private const val DEFAULT_POOL_SIZE = 1
        private const val MAX_POOL_SIZE = 2
        private const val KEEP_ALIVE_TIME_MS: Long = 0
        private const val THREAD_NAME_PREFIX = "BTT-"
    }
}