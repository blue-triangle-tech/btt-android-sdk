package com.bluetriangle.analytics.applaunch

import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.event.BTTEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AppLaunchReporter {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var appInstallReportJob: Job? = null

    fun reportAppInstall(installTime: Long) {
        appInstallReportJob?.cancel()

        appInstallReportJob = scope.launch {
            while (Tracker.instance == null) delay(5)

            Timer().apply {
                startWithoutPerformanceMonitor()
                setPageName(BTTEvent.AppInstall.defaultPageName)
                setContentGroupName(BTTEvent.AppInstall.defaultPageName)
                setTrafficSegmentName(BTTEvent.AppInstall.defaultPageName)
                setTimeOnPage(Constants.TIMER_MIN_PGTM)
                pageTimeCalculator = {
                    Constants.TIMER_MIN_PGTM
                }
                generateNativeAppProperties()
                nativeAppProperties.loadTime = Constants.TIMER_MIN_PGTM
                nativeAppProperties.event = BTTEvent.AppInstall
                nativeAppProperties.installTime = installTime

                submit()

                appInstallReportJob = null
            }
        }
    }
}