package com.bluetriangle.analytics.launchtime.helpers

import android.util.Log
import com.bluetriangle.analytics.launchtime.LogData
import com.bluetriangle.analytics.launchtime.LogHolder
import com.bluetriangle.analytics.launchtime.model.AppLifecycleEvent
import com.bluetriangle.analytics.launchtime.model.LaunchType
import java.util.LinkedList
import java.util.Queue

internal class AppEventAccumulator(private val logHolder: LogHolder) {

    private var events: Queue<AppLifecycleEvent> = LinkedList()

    fun accumulate(event: AppLifecycleEvent): Result? {
        events.offer(event)
        if (event is AppLifecycleEvent.ActivityResumed) {
            val appEvents = events.toList()
            events.clear()

            return when (appEvents.first()) {
                is AppLifecycleEvent.AppLifecycleCreated -> Result(LaunchType.Cold, appEvents)
                is AppLifecycleEvent.ActivityCreated -> Result(LaunchType.Warm, appEvents)
                is AppLifecycleEvent.ActivityStarted -> Result(LaunchType.Hot, appEvents)
                else -> {
                    logHolder.log(
                        LogData(
                            level = Log.ERROR,
                            "Invalid App Event State: Activity.onResumed received but, Application.onCreate or Activity.onStart not received."
                        )
                    )
                    null
                }
            }
        }
        return null
    }

    internal fun reset() {
        events.clear()
    }

    class Result(val type: LaunchType, val events: List<AppLifecycleEvent>)
}