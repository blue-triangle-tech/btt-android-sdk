/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

internal open class BTTRemoteConfiguration(
    val networkSampleRate: Double?,
    val ignoreScreens:List<String>,
    val enableRemoteConfigAck: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if(other is BTTRemoteConfiguration) {
            var isIgnoreListSame = ignoreScreens.size == other.ignoreScreens.size

            if(isIgnoreListSame) {
                for(i in ignoreScreens.indices) {
                    if(ignoreScreens[i] != other.ignoreScreens[i]) {
                        isIgnoreListSame = false
                        break
                    }
                }
            }

            return other.networkSampleRate == this.networkSampleRate &&
                    other.enableRemoteConfigAck == this.enableRemoteConfigAck &&
                    isIgnoreListSame
        }
        return false
    }

    override fun toString(): String {
        return "RemoteConfig { networkSampleRate: $networkSampleRate, ignoreList: ${ignoreScreens}, enableRemoteConfigAck: $enableRemoteConfigAck }"
    }

    override fun hashCode(): Int {
        var result = networkSampleRate?.hashCode() ?: 0
        result = 31 * result + enableRemoteConfigAck.hashCode()
        return result
    }
}