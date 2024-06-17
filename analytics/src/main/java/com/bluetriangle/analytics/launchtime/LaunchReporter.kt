@file:OptIn(DelicateCoroutinesApi::class)

package com.bluetriangle.analytics.launchtime

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.launchtime.data.LaunchEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class LaunchReporter(
    private val logger: Logger?,
    private val launchEventProducer: LaunchEventProducer
) {

    init {
        logger?.debug("Started launch time reporting...")
        GlobalScope.launch {
            for (event in launchEventProducer.launchEvents) {
                val launchPageName = when (event) {
                    is LaunchEvent.HotLaunch -> "HotLaunchTime"
                    is LaunchEvent.WarmLaunch -> "WarmLaunchTime"
                    is LaunchEvent.ColdLaunch -> "ColdLaunchTime"
                }

                // As Reporter is initialized inside the Tracker constructor.
                // The Tracker instance won't be immediately available in the singleton.
                // So if we already have some launch event in the channel when the Reporter was initialized,
                // it immediately tries to report the launch which would not be possible
                // due to Tracker.instance being null. So we wait till the Tracker.instance has something in it before reporting launch.
                while (Tracker.instance == null) delay(5)

                reportLaunch(
                    launchPageName,
                    event.data.startTime,
                    event.data.duration,
                    event.data.activityName
                )
                logger?.info("Submitted launch event: ${event.data.type} Launch which took ${event.data.duration} ms")
            }
        }
    }

    private fun reportLaunch(
        launchPageName: String, startTime: Long, duration: Long, launchActivityName: String
    ) {
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
            submit()
        }

    }
}