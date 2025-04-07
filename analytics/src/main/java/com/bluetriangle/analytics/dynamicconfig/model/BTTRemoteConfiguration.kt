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
    val clarityProjectID: String?,
    val clarityEnabled: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if(other is BTTRemoteConfiguration) {
            return other.networkSampleRate == this.networkSampleRate &&
                    other.enableRemoteConfigAck == this.enableRemoteConfigAck &&
                    other.ignoreScreens.joinToString() == ignoreScreens.joinToString() &&
                    other.enableAllTracking == enableAllTracking &&
                    other.clarityProjectID == clarityProjectID &&
                    other.clarityEnabled == clarityEnabled
        }
        return false
    }

    override fun toString(): String {
        return "RemoteConfig { networkSampleRate: $networkSampleRate, ignoreList: ${ignoreScreens}, enableRemoteConfigAck: $enableRemoteConfigAck, enableAllTracking: $enableAllTracking, clarityProjectID: $clarityProjectID, clarityEnabled: $clarityEnabled }"
    }

    override fun hashCode(): Int {
        var result = networkSampleRate?.hashCode() ?: 0
        result = 31 * result + ignoreScreens.hashCode()
        result = 31 * result + enableAllTracking.hashCode()
        result = 31 * result + enableRemoteConfigAck.hashCode()
        result = 31 * result + (clarityProjectID?.hashCode() ?: 0)
        result = 31 * result + clarityEnabled.hashCode()
        return result
    }

}