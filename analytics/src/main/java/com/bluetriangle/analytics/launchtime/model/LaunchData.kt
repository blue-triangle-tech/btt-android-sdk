package com.bluetriangle.analytics.launchtime.model

internal data class LaunchData(
    val activityName:String,
    val startTime:Long,
    val endTime:Long,
    val type: LaunchType
) {
    val duration:Long
        get() = endTime - startTime
}