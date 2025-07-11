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
    val groupingEnabled: Boolean,
    val groupingIdleTime: Int
) {
    override fun equals(other: Any?): Boolean {
        if(other is BTTRemoteConfiguration) {
            return other.networkSampleRate == this.networkSampleRate &&
                    other.enableRemoteConfigAck == this.enableRemoteConfigAck &&
                    other.ignoreScreens.joinToString() == ignoreScreens.joinToString() &&
                    other.enableAllTracking == enableAllTracking &&
                    other.enableScreenTracking == enableScreenTracking &&
                    other.groupingEnabled == groupingEnabled &&
                    other.groupingIdleTime == groupingIdleTime
        }
        return false
    }

    override fun toString(): String {
        return "RemoteConfig { networkSampleRate: $networkSampleRate, ignoreList: ${ignoreScreens}, enableRemoteConfigAck: $enableRemoteConfigAck, enableAllTracking: $enableAllTracking,  enableScreenTracking: $enableScreenTracking, groupingEnabled: $groupingEnabled, groupingIdleTime: $groupingIdleTime }"
    }

    override fun hashCode(): Int {
        var result = networkSampleRate?.hashCode() ?: 0
        result = 31 * result + enableAllTracking.hashCode()
        result = 31 * result + enableRemoteConfigAck.hashCode()
        result = 31 * result + enableScreenTracking.hashCode()
        result = 31 * result + groupingEnabled.hashCode()
        result = 31 * result + groupingIdleTime
        result = 31 * result + ignoreScreens.hashCode()
        return result
    }

}