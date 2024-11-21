/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import org.json.JSONObject

internal open class BTTRemoteConfiguration(
    val networkSampleRate: Double
) {
    companion object {
        fun fromJson(remoteConfigJson:JSONObject) = BTTRemoteConfiguration(
            remoteConfigJson.getInt("networkSampleRateSDK")/100.0
        )
    }

    override fun toString(): String {
        return "RemoteConfig { networkSampleRate: $networkSampleRate }"
    }
}