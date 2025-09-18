package com.bluetriangle.analytics.performancemonitor

import android.os.Build
import android.os.SystemClock
import android.system.Os
import android.system.OsConstants
import android.util.Log
import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Timer.Companion.FIELD_PAGE_NAME
import com.bluetriangle.analytics.utility.getNumberOfCPUCores
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Locale

internal class CpuMonitor(configuration: BlueTriangleConfiguration) : MetricMonitor {

    var totalClockTicsLastCollection = 0L
    var elapsedTimeLastCollection = 0.0

    var isVerboseDebug = configuration.isDebug && configuration.debugLevel == Log.VERBOSE

    companion object {
        /**
         * /proc/pid/stat file contains the information about the CPU usage for the process with pid = <pid>
         * Where pid can be replaced with "self" to get the current process's stat
         * To get information about it's fields refer to
         * @link https://web.archive.org/web/20130302063336/http://www.lindevdoc.org/wiki//proc/pid/stat
         */
        private val CPU_STATS_FILE = File("/proc/self/stat")
    }

    override val metricFields: Map<PerformanceMetric, String>
        get() = mapOf(
            PerformanceMetric.MinCpu to minCpu.toString(),
            PerformanceMetric.MaxCpu to maxCpu.toString(),
            PerformanceMetric.AvgCpu to avgCpu.toString()
        )

    private val logger = configuration.logger
    private var minCpu = Double.MAX_VALUE
    private var maxCpu = 0.0
    private var cumulativeCpu = 0.0
    private var cpuCount: Long = 0
    private var cpuUsed = arrayListOf<Double>()

    private val clockSpeedHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Os.sysconf(OsConstants._SC_CLK_TCK)
    } else {
        0
    }

    private val cpuCoresCount = getNumberOfCPUCores() ?: 0L

    private val avgCpu: Double
        get() = if (cpuCount == 0L) 0.0  else cumulativeCpu / cpuCount

    private fun updateCpu(cpu: Double) {
        if (cpu < minCpu) {
            minCpu = cpu
        }
        if (cpu > maxCpu) {
            maxCpu = cpu
        }
        if (isVerboseDebug) {
            cpuUsed.add(cpu)
        }
        cumulativeCpu += cpu
        cpuCount++
    }

    private fun readCPUClocksUsed(): Long? {
        return try {
            val stats = BufferedReader(FileReader(CPU_STATS_FILE)).use { it.readLine() }
            getTotalCPUClocksUsed(stats)
        } catch (e: IOException) {
            logger?.error(e, "Error reading CPU info")
            null
        }
    }

    override fun onBeforeSleep() {
        /*
         * Only for the first time, we initialize the elapsedTime and cpu clocktics used
         * so we can use them while calculating the cpu usage
         */
        if (totalClockTicsLastCollection == 0L) {
            elapsedTimeLastCollection = getElapsedSystemTime()
            totalClockTicsLastCollection = readCPUClocksUsed() ?: 0L
        }
    }

    override fun onAfterSleep() {
        val elapsedTime = getElapsedSystemTime()
        val totalClockTicks = if (clockSpeedHz > 0) readCPUClocksUsed() else return
        if (totalClockTicks != null) {
            // Time delta denotes the difference between now and the last usage collection
            val timeDelta = elapsedTime - elapsedTimeLastCollection
            // Clock tick delta denotes the amount of clock ticks used by the process
            // since the last collection
            val clockTicksDelta = totalClockTicks - totalClockTicsLastCollection

            val cpuUsage = calculateCPUUsage(clockTicksDelta, timeDelta).coerceAtMost(100.0)
            updateCpu(cpuUsage)
            totalClockTicsLastCollection = totalClockTicks
            elapsedTimeLastCollection = elapsedTime
            logger?.log(Log.VERBOSE, String.format(Locale.ENGLISH, "CPU Usage: %.2f", cpuUsage))
        }
    }

    /**
     * Calculates the CPU usage
     * @param usedClockTicks The amount of clock ticks the process used for a duration
     * @param timeDuration The time duration for which the clock ticks are measured
     */
    private fun calculateCPUUsage(usedClockTicks: Long, timeDuration: Double): Double {
        // calculating max cpu usage from hertz which is clock ticks per second
        // so if the clock speed is 100 Hz, it means there are 100 clock ticks in a second
        // so we just calculate how many max clock ticks could've happened in the timeDelta time duration
        val maxClockTicks = (timeDuration * clockSpeedHz)
        val totalCPUUsage = usedClockTicks / maxClockTicks * 100.0
        return totalCPUUsage / cpuCoresCount
    }

    /**
     * For more accuracy we are using elapsedRealtimeNano() which returns nanoseconds
     * and while converting nanoseconds to seconds we are dividing it with a double value
     * instead of doing simple integer division
     */
    private fun getElapsedSystemTime(): Double {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            SystemClock.elapsedRealtimeNanos() / 1_000_000_000.0
        } else {
            SystemClock.elapsedRealtime() / 1_000.0
        }
    }

    /**
     * Returns the total amount of CPU clocks used since the process was started.
     *
     * Uses the following columns from the /proc/pid/stat file:
     *
     * 14. utime - CPU time spent in user code, measured in jiffies
     * 15. stime - CPU time spent in kernel code, measured in jiffies
     * 16. cutime - CPU time spent in user code, including time from children
     * 17. cstime - CPU time spent in kernel code, including time from children
     * All these values are measured in cpu clock ticks or jiffies.
     *
     */
    private fun getTotalCPUClocksUsed(stats: String): Long {
        val statsList = stats.split(" ")
        val uTime = statsList[13].toLong()
        val sTime = statsList[14].toLong()
        val cuTime = statsList[15].toLong()
        val csTime = statsList[16].toLong()
        return uTime + sTime + cuTime + csTime
    }

    override fun onTimerSubmit(timer: Timer) {
        super.onTimerSubmit(timer)
        if (isVerboseDebug) {
            val pageName = timer.getField(FIELD_PAGE_NAME)
            logger?.log(Log.VERBOSE, "CPU Samples for $pageName : $cpuUsed")
        }
    }
}