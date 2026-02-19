package com.bluetriangle.analytics.launchtime.model

import com.bluetriangle.analytics.event.BTTEvent

internal sealed class LaunchEvent(val event: BTTEvent, val data: LaunchData) {

    constructor(event: BTTEvent, activityName:String, startTime:Long, type: LaunchType):this(event, LaunchData(activityName, startTime, System.currentTimeMillis(), type))

    val pageName: String
        get() = event.defaultPageName

    class HotLaunch(activityName:String, startTime:Long) : LaunchEvent(
        BTTEvent.HotLaunch, activityName, startTime,
        LaunchType.Hot
    )

    class WarmLaunch(activityName:String, startTime:Long) : LaunchEvent(
        BTTEvent.WarmLaunch, activityName, startTime,
        LaunchType.Warm
    )

    class ColdLaunch(activityName: String, startTime: Long) : LaunchEvent(
        BTTEvent.ColdLaunch, activityName, startTime,
        LaunchType.Cold
    )

    companion object {
        fun create(type: LaunchType, activityName: String, startTime: Long): LaunchEvent {
            return when(type) {
                LaunchType.Hot -> HotLaunch(activityName, startTime)
                LaunchType.Warm -> WarmLaunch(activityName, startTime)
                LaunchType.Cold -> ColdLaunch(activityName, startTime)
            }
        }
    }

}