/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

internal open class BTTRemoteConfiguration(
    val networkSampleRate: Double?,
    val ignoreScreens:List<String>,
    val enableAllTracking: Boolean,
    val enableRemoteConfigAck: Boolean,
    val enableScreenTracking: Boolean,
    val enableGrouping: Boolean,
    val groupingIdleTime: Int,
    val enableGroupingTapDetection: Boolean,
    val enableNetworkStateTracking: Boolean,
    val enableCrashTracking: Boolean,
    val enableANRTracking: Boolean,
    val enableMemoryWarning: Boolean,
    val enableLaunchTime: Boolean,
    val enableWebViewStitching: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if(other is BTTRemoteConfiguration) {
            return other.networkSampleRate == this.networkSampleRate &&
                    other.enableRemoteConfigAck == this.enableRemoteConfigAck &&
                    other.ignoreScreens.joinToString() == ignoreScreens.joinToString() &&
                    other.enableAllTracking == enableAllTracking &&
                    other.enableScreenTracking == enableScreenTracking &&
                    other.enableGrouping == enableGrouping &&
                    other.groupingIdleTime == groupingIdleTime &&
                    other.enableGroupingTapDetection == enableGroupingTapDetection &&
                    other.enableNetworkStateTracking == enableNetworkStateTracking &&
                    other.enableCrashTracking == enableCrashTracking &&
                    other.enableANRTracking == enableANRTracking &&
                    other.enableMemoryWarning == enableMemoryWarning &&
                    other.enableLaunchTime == enableLaunchTime &&
                    other.enableWebViewStitching == enableWebViewStitching
        }
        return false
    }

    override fun toString(): String {
        return "RemoteConfig { networkSampleRate: $networkSampleRate, ignoreList: ${ignoreScreens}, enableRemoteConfigAck: $enableRemoteConfigAck, enableAllTracking: $enableAllTracking,  enableScreenTracking: $enableScreenTracking, enableGrouping: $enableGrouping, groupingIdleTime: $groupingIdleTime, enableGroupingTapDetection: $enableGroupingTapDetection, enableNetworkStateTracking: $enableNetworkStateTracking, enableCrashTracking: $enableCrashTracking, enableANRTracking: $enableANRTracking, enableMemoryWarning: $enableMemoryWarning, enableLaunchTime: $enableLaunchTime, enableWebViewStitching: $enableWebViewStitching }"
    }

    override fun hashCode(): Int {
        var result = networkSampleRate?.hashCode() ?: 0
        result = 31 * result + enableAllTracking.hashCode()
        result = 31 * result + enableRemoteConfigAck.hashCode()
        result = 31 * result + enableScreenTracking.hashCode()
        result = 31 * result + enableGrouping.hashCode()
        result = 31 * result + groupingIdleTime
        result = 31 * result + ignoreScreens.hashCode()
        result = 31 * result + enableGroupingTapDetection.hashCode()
        result = 31 * result + enableNetworkStateTracking.hashCode()
        result = 31 * result + enableCrashTracking.hashCode()
        result = 31 * result + enableANRTracking.hashCode()
        result = 31 * result + enableMemoryWarning.hashCode()
        result = 31 * result + enableLaunchTime.hashCode()
        result = 31 * result + enableWebViewStitching.hashCode()
        return result
    }

}