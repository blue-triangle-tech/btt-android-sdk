package com.bluetriangle.analytics.launchtime.helpers

import android.util.Log
import com.bluetriangle.analytics.launchtime.LogData
import com.bluetriangle.analytics.launchtime.LogHolder
import com.bluetriangle.analytics.launchtime.data.AppEvent
import com.bluetriangle.analytics.launchtime.data.LaunchType
import java.util.LinkedList
import java.util.Queue

internal class AppEventAccumulator(private val logHolder: LogHolder) {

    private var events: Queue<AppEvent> = LinkedList()

    fun accumulate(event: AppEvent): Result? {
        events.offer(event)
        if (event is AppEvent.ActivityResumed) {
            val appEvents = events.toList()
            events.clear()

            return when (appEvents.first()) {
                is AppEvent.AppCreated -> Result(LaunchType.Cold, appEvents)
                is AppEvent.ActivityCreated -> Result(LaunchType.Warm, appEvents)
                is AppEvent.ActivityStarted -> Result(LaunchType.Hot, appEvents)
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

    class Result(val type: LaunchType, val events: List<AppEvent>)
}