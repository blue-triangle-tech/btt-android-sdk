package com.bluetriangle.analytics

import android.app.ActivityManager
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.system.Os
import android.system.OsConstants
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * CPU monitoring adapted from https://eng.lyft.com/monitoring-cpu-performance-of-lyfts-android-applications-4e36fafffe12
 */
class PerformanceMonitor(configuration: BlueTriangleConfiguration) : Thread(THREAD_NAME) {
    private val logger = configuration.logger
    private val activityManager = Tracker.instance?.activityManager
    private var isRunning = true
    private val interval = configuration.performanceMonitorIntervalMs

    private var minMemory = Long.MAX_VALUE
    private var maxMemory: Long = 0
    private var cumulativeMemory: Long = 0
    private var memoryCount: Long = 0

    private var minCpu = Double.MAX_VALUE
    private var maxCpu = 0.0
    private var cumulativeCpu = 0.0
    private var cpuCount: Long = 0

    private var lastCpuInfo: CpuInfo? = null
    private val clockSpeedHz = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Os.sysconf(OsConstants._SC_CLK_TCK)
    } else {
        0
    }

    override fun run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST)
        while (isRunning) {
            try {
                val memoryInfo = ActivityManager.MemoryInfo()
                activityManager?.getMemoryInfo(memoryInfo)
                val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
                logger?.debug("Used Memory: $usedMemory", )
                updateMemory(usedMemory)
                if (lastCpuInfo == null) {
                    lastCpuInfo = if (clockSpeedHz > 0) readCpuInfo() else null
                }
                if (isRunning) {
                    sleep(interval)
                }
                val cpuInfo = if (clockSpeedHz > 0) readCpuInfo() else null
                if (lastCpuInfo != null && cpuInfo != null) {
                    val cpuTimeDeltaSec = cpuInfo.cpuTime - lastCpuInfo!!.cpuTime
                    val processTimeDeltaSec = cpuInfo.processTime - lastCpuInfo!!.processTime
                    val relAvgUsagePercent = cpuTimeDeltaSec / processTimeDeltaSec * 100.0
                    updateCpu(relAvgUsagePercent)
                    logger?.debug("CPU Usage: %f", relAvgUsagePercent)
                }
            } catch (e: InterruptedException) {
                logger?.error(e, "Performance Monitor thread interrupted")
            }
        }
    }

    private fun updateMemory(memory: Long) {
        if (memory < minMemory) {
            minMemory = memory
        }
        if (memory > maxMemory) {
            maxMemory = memory
        }
        cumulativeMemory += memory
        memoryCount++
    }

    private fun updateCpu(cpu: Double) {
        if (cpu < minCpu) {
            minCpu = cpu
        }
        if (cpu > maxCpu) {
            maxCpu = cpu
        }
        cumulativeCpu += cpu
        cpuCount++
    }

    fun stopRunning() {
        isRunning = false
    }

    val performanceReport: PerformanceReport
        get() {
            val averageMemory = calculateAverageMemory()
            val averageCpu = calculateAverageCpu()
            return PerformanceReport(minMemory, maxMemory, averageMemory, minCpu, maxCpu, averageCpu)
        }

    private fun calculateAverageMemory(): Long {
        return if (memoryCount == 0L) {
            0
        } else cumulativeMemory / memoryCount
    }

    private fun calculateAverageCpu(): Double {
        return if (cpuCount == 0L) {
            0.0
        } else cumulativeCpu / cpuCount
    }

    private fun readCpuInfo(): CpuInfo? {
        return try {
            val stats = BufferedReader(FileReader(CPU_STATS_FILE)).use { it.readLine() }
            CpuInfo.fromStats(clockSpeedHz, stats)
        } catch (e: IOException) {
            logger?.error(e, "Error reading CPU info")
            null
        }
    }


    private data class CpuInfo(
        val clockSpeedHz: Long, // the number of clock ticks per second, measured in Hertz
        val uptimeSec: Long, // the time since the device booted, measured in seconds.
        val utime: Long, // the amount of time that this process has been scheduled in user mode, measured in clock ticks.
        val stime: Long, // the amount of time that this process has been scheduled in kernel mode, measured in clock ticks.
        val cutime: Long, // the amount of time that this process’ waited-for children have been scheduled in user mode, measured in clock ticks.
        val cstime: Long, // the amount of time that this process’ waited-for children have been scheduled in kernel mode, measured in clock ticks.
        val startTime: Long, // the time the process started after system boot, measured in clock ticks.
    ) {
        /**
         * @return the time since the application launched, measured in seconds.
         */
        val processTime: Long
            get() = uptimeSec - startTime / clockSpeedHz

        /**
         * @return the time CPU spent doing work for a given application process, and is measured in seconds.
         */
        val cpuTime: Long
            get() = (utime + stime + cutime + cstime) / clockSpeedHz

        override fun toString(): String {
            return "CpuInfo{clockSpeedHz=$clockSpeedHz, uptimeSec=$uptimeSec, utime=$utime, stime=$stime, cutime=$cutime, cstime=$cstime, startTime=$startTime}"
        }

        companion object {
            fun fromStats(clockSpeedHz: Long, stats: String): CpuInfo {
                val statsList = stats.split(" ")
                return CpuInfo(
                    clockSpeedHz,
                    SystemClock.elapsedRealtime() / 1000,
                    statsList[13].toLong(),
                    statsList[14].toLong(),
                    statsList[15].toLong(),
                    statsList[16].toLong(),
                    statsList[21].toLong(),
                )
            }
        }
    }

    companion object {
        private const val THREAD_NAME = "BTTPerformanceMonitor"
        private val CPU_STATS_FILE = File("/proc/self/stat")
    }
}