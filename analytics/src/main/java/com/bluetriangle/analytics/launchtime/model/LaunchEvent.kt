package com.bluetriangle.analytics.launchtime.model

import com.bluetriangle.analytics.screenTracking.EventID

internal sealed class LaunchEvent private constructor(val eventID: EventID, val name: String, val data: LaunchData) {

    constructor(eventID: EventID, name: String, activityName:String, startTime:Long, type: LaunchType):this(eventID, name, LaunchData(activityName, startTime, System.currentTimeMillis(), type))

    class HotLaunch(activityName:String, startTime:Long) : LaunchEvent(
        EventID.HotLaunch, "HotLaunchTime", activityName, startTime,
        LaunchType.Hot
    )

    class WarmLaunch(activityName:String, startTime:Long) : LaunchEvent(
        EventID.WarmLaunch, "WarmLaunchTime", activityName, startTime,
        LaunchType.Warm
    )

    class ColdLaunch(activityName: String, startTime: Long) : LaunchEvent(
        EventID.ColdLaunch, "ColdLaunchTime", activityName, startTime,
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