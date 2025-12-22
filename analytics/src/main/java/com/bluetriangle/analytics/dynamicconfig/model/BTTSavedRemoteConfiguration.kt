/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

internal class BTTSavedRemoteConfiguration(
    networkSampleRate: Double?,
    ignoreScreens: List<String>,
    enableRemoteConfigAck: Boolean,
    enableAllTracking: Boolean,
    enableScreenTracking: Boolean,
    enableGrouping: Boolean,
    groupingIdleTime: Int,
    groupedViewSampleRate: Double?,
    enableGroupingTapDetection: Boolean,
    enableNetworkStateTracking: Boolean,
    enableCrashTracking: Boolean,
    enableANRTracking: Boolean,
    enableMemoryWarning: Boolean,
    enableLaunchTime: Boolean,
    enableWebViewStitching: Boolean,
    val savedDate: Long
) : BTTRemoteConfiguration(
    networkSampleRate,
    ignoreScreens,
    enableAllTracking,
    enableRemoteConfigAck,
    enableScreenTracking,
    enableGrouping,
    groupingIdleTime,
    groupedViewSampleRate,
    enableGroupingTapDetection,
    enableNetworkStateTracking,
    enableCrashTracking,
    enableANRTracking,
    enableMemoryWarning,
    enableLaunchTime,
    enableWebViewStitching
) {

    companion object {
        fun from(remoteConfig: BTTRemoteConfiguration) = BTTSavedRemoteConfiguration(
            remoteConfig.networkSampleRate,
            remoteConfig.ignoreScreens,
            remoteConfig.enableRemoteConfigAck,
            remoteConfig.enableAllTracking,
            remoteConfig.enableScreenTracking,
            remoteConfig.enableGrouping,
            remoteConfig.groupingIdleTime,
            remoteConfig.groupedViewSampleRate,
            remoteConfig.enableGroupingTapDetection,
            remoteConfig.enableNetworkStateTracking,
            remoteConfig.enableCrashTracking,
            remoteConfig.enableANRTracking,
            remoteConfig.enableMemoryWarning,
            remoteConfig.enableLaunchTime,
            remoteConfig.enableWebViewStitching,
            System.currentTimeMillis()
        )
    }

    override fun equals(other: Any?): Boolean {
        if(other is BTTSavedRemoteConfiguration) {
            return super.equals(other) && this.savedDate == other.savedDate
        }
        if(other is BTTRemoteConfiguration) {
            return super.equals(other)
        }
        return false
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + savedDate.hashCode()
        return result
    }

}
