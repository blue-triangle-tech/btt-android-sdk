/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.utility.getBooleanOrNull
import com.bluetriangle.analytics.utility.getIntOrNull
import org.json.JSONObject

internal object BTTRemoteConfigurationMapper {

    internal const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"

    fun fromJson(remoteConfigJson: JSONObject): BTTRemoteConfiguration {
        val networkSampleRate = remoteConfigJson.getIntOrNull(NETWORK_SAMPLE_RATE)?.div(100.0)
        val enableRemoteConfig = remoteConfigJson.getBooleanOrNull(ENABLE_REMOTE_CONFIG)?:false

        return BTTRemoteConfiguration(
            networkSampleRate,
            enableRemoteConfig
        )
    }
}