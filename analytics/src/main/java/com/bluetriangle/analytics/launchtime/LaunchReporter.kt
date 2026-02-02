@file:OptIn(DelicateCoroutinesApi::class)

package com.bluetriangle.analytics.launchtime

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.launchtime.model.LaunchEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class LaunchReporter(
    private val logger: Logger?,
    private val launchEventProducer: LaunchEventProducer
) {

    private var launchReporterJob: Job? = null

    fun start() {
        logger?.debug("Started launch time reporting...")
        launchReporterJob = GlobalScope.launch {
            for (event in launchEventProducer.launchEvents) {
                // As Reporter is initialized inside the Tracker constructor.
                // The Tracker instance won't be immediately available in the singleton.
                // So if we already have some launch event in the channel when the Reporter was initialized,
                // it immediately tries to report the launch which would not be possible
                // due to Tracker.instance being null. So we wait till the Tracker.instance has something in it before reporting launch.
                while (Tracker.instance == null) delay(5)

                reportLaunch(event)
                logger?.info("Submitted launch event: ${event.data.type} Launch which took ${event.data.duration} ms")
            }
        }
    }

    fun stop() {
        launchReporterJob?.cancel()
        launchReporterJob = null
    }

    private fun reportLaunch(
        event: LaunchEvent
    ) {
        val launchPageName = event.name
        val startTime = event.data.startTime
        val duration = event.data.duration
        val launchActivityName = event.data.activityName

        Timer().apply {
            startWithoutPerformanceMonitor()
            setField(Timer.FIELD_UNLOAD_EVENT_START, startTime)
            setPageName(launchPageName)
            setContentGroupName("LaunchTime")
            setTimeOnPage(duration)
            pageTimeCalculator = {
                duration
            }
            generateNativeAppProperties()
            nativeAppProperties.loadTime = duration
            nativeAppProperties.launchScreenName = launchActivityName
            nativeAppProperties.eventID = event.eventID
            submit()
        }

    }
}