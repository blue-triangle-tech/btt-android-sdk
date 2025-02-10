/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

internal class BTTSavedRemoteConfiguration(
    networkSampleRate: Double?,
    ignoreList: List<String>,
    enableRemoteConfigAck: Boolean,
    enableAllTracking: Boolean,
    val savedDate: Long
) : BTTRemoteConfiguration(networkSampleRate, ignoreList, enableAllTracking, enableRemoteConfigAck) {

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
