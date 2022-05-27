package com.bluetriangle.analytics;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * CPU monitoring adapted from https://eng.lyft.com/monitoring-cpu-performance-of-lyfts-android-applications-4e36fafffe12
 */
public class PerformanceMonitor extends Thread {
    private static final String THREAD_NAME = "BTTPerformanceMonitor";
    private static final File CPU_STATS_FILE = new File("/proc/self/stat");

    @NonNull private final Logger logger;
    @NonNull private final ActivityManager activityManager;
    private boolean isRunning = true;
    private final long interval;

    private long minMemory = Long.MAX_VALUE;
    private long maxMemory = 0;
    private long cumulativeMemory = 0;
    private long memoryCount = 0;

    private double minCpu = Long.MAX_VALUE;
    private double maxCpu = 0;
    private double cumulativeCpu = 0;
    private long cpuCount = 0;
    private @Nullable CpuInfo lastCpuInfo = null;
    private final long clockSpeedHz;

    public PerformanceMonitor(@NonNull final BlueTriangleConfiguration configuration) {
        super(THREAD_NAME);
        this.logger = configuration.getLogger();
        this.activityManager = Tracker.getInstance().getActivityManager();
        this.interval = configuration.getPerformanceMonitorIntervalMs();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clockSpeedHz = Os.sysconf(OsConstants._SC_CLK_TCK);
        } else {
            clockSpeedHz = 0;
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        while (isRunning) {
            try {
                final ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                activityManager.getMemoryInfo(memoryInfo);
                final long usedMemory = memoryInfo.totalMem - memoryInfo.availMem;
                logger.debug("Used Memory: %d", usedMemory);
                updateMemory(usedMemory);
                if (lastCpuInfo == null) {
                    lastCpuInfo = clockSpeedHz > 0 ? readCpuInfo() : null;
                }
                if (isRunning) {
                    Thread.sleep(interval);
                }
                final CpuInfo cpuInfo = clockSpeedHz > 0 ? readCpuInfo() : null;
                if(lastCpuInfo != null && cpuInfo != null) {
                    final long cpuTimeDeltaSec = cpuInfo.getCpuTime() - lastCpuInfo.getCpuTime();
                    final long processTimeDeltaSec = cpuInfo.getProcessTime() - lastCpuInfo.getProcessTime();
                    final double relAvgUsagePercent = ((double)cpuTimeDeltaSec / processTimeDeltaSec) * 100.0;
                    updateCpu(relAvgUsagePercent);
                    logger.debug("CPU Usage: %f", relAvgUsagePercent);
                }
            } catch (InterruptedException e) {
                logger.error(e, "Performance Monitor thread interrupted");
            }
        }
    }

    private void updateMemory(final Long memory) {
        if (memory < minMemory) {
            minMemory = memory;
        }
        if (memory > maxMemory) {
            maxMemory = memory;
        }
        cumulativeMemory += memory;
        memoryCount++;
    }

    private void updateCpu(final double cpu) {
        if (cpu < minCpu) {
            minCpu = cpu;
        }
        if (cpu > maxCpu) {
            maxCpu = cpu;
        }
        cumulativeCpu += cpu;
        cpuCount++;
    }

    public void stopRunning() {
        isRunning = false;
    }

    public PerformanceReport getPerformanceReport() {
        final long averageMemory = calculateAverageMemory();
        final double averageCpu = calculateAverageCpu();
        return new PerformanceReport(minMemory, maxMemory, averageMemory, minCpu, maxCpu, averageCpu);
    }

    private long calculateAverageMemory() {
        if (memoryCount == 0) {
            return 0;
        }
        return cumulativeMemory / memoryCount;
    }

    private double calculateAverageCpu() {
        if (cpuCount == 0) {
            return 0;
        }
        return cumulativeCpu / cpuCount;
    }

    private @Nullable CpuInfo readCpuInfo() {
        try {
            final BufferedReader fileReader = new BufferedReader(new FileReader(CPU_STATS_FILE));
            final String stats = fileReader.readLine();
            fileReader.close();
            final CpuInfo cpuInfo = new CpuInfo(clockSpeedHz, stats);
            return cpuInfo;
        } catch (IOException e) {
            logger.error(e, "Error reading CPU info");
            return null;
        }
    }

    private static class CpuInfo {
        /**
         * the number of clock ticks per second, measured in Hertz
         */
        final long clockSpeedHz;

        /**
         * the time since the device booted, measured in seconds.
         */
        final long uptimeSec;

        /**
         * the amount of time that this process has been scheduled in user mode, measured in clock ticks.
         */
        final long utime;

        /**
         * the amount of time that this process has been scheduled in kernel mode, measured in clock ticks.
         */
        final long stime;

        /**
         * the amount of time that this process’ waited-for children have been scheduled in user mode, measured in clock ticks.
         */
        final long cutime;

        /**
         * the amount of time that this process’ waited-for children have been scheduled in kernel mode, measured in clock ticks.
         */
        final long cstime;

        /**
         * the time the process started after system boot, measured in clock ticks.
         */
        final long startTime;

        public CpuInfo(final long clockSpeedHz, final String stats) {
            final String[] statsArray = stats.split(" ");
            uptimeSec = SystemClock.elapsedRealtime() / 1000;
            this.clockSpeedHz = clockSpeedHz;
            utime = Long.parseLong(statsArray[13]);
            stime = Long.parseLong(statsArray[14]);
            cutime = Long.parseLong(statsArray[15]);
            cstime = Long.parseLong(statsArray[16]);
            startTime = Long.parseLong(statsArray[21]);
        }

        /**
         * @return the time since the application launched, measured in seconds.
         */
        public long getProcessTime() {
            return uptimeSec - (startTime / clockSpeedHz);
        }

        /**
         * @return the time CPU spent doing work for a given application process, and is measured in seconds.
         */
        public long getCpuTime() {
            return (utime + stime + cutime + cstime) / clockSpeedHz;
        }

        @NonNull
        @Override
        public String toString() {
            return "CpuInfo{" +
                    "clockSpeedHz=" + clockSpeedHz +
                    ", uptimeSec=" + uptimeSec +
                    ", utime=" + utime +
                    ", stime=" + stime +
                    ", cutime=" + cutime +
                    ", cstime=" + cstime +
                    ", startTime=" + startTime +
                    '}';
        }
    }
}
