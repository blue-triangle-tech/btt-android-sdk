@file:OptIn(DelicateCoroutinesApi::class)

package com.bluetriangle.analytics.launchtime

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.launchtime.data.LaunchEvent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal class LaunchReporter(val launchEventProducer: LaunchEventProducer) {

    private val logger = Tracker.instance?.configuration?.logger

    fun start() {
        logger?.debug("Started launch time reporting...")
        GlobalScope.launch {
            for(event in launchEventProducer.launchEvents) {
                val launchPageName = if(event is LaunchEvent.HotLaunch) "HotLaunchTime" else "ColdLaunchTime"
                val duration = event.data.duration
                val launchActivityName = event.data.activityName

                reportLaunch(launchPageName, duration, launchActivityName)
                logger?.debug("Submitted launch event: ${event.data.type} Launch which took ${event.data.duration} ms")
            }
        }
    }

    private fun reportLaunch(
        launchPageName: String,
        duration: Long,
        launchActivityName: String
    ) {
        Timer().apply {
            startWithoutPerformanceMonitor()
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