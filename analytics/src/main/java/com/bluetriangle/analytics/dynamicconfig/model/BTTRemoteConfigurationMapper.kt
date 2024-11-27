package com.bluetriangle.analytics.dynamicconfig.model

import org.json.JSONObject

internal object BTTRemoteConfigurationMapper {

    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"

    fun fromJson(remoteConfigJson: JSONObject): BTTRemoteConfiguration {
        if (!remoteConfigJson.has(NETWORK_SAMPLE_RATE)) {
            throw MissingFieldException(arrayOf(NETWORK_SAMPLE_RATE))
        }
        val networkSampleRate = remoteConfigJson.getInt(NETWORK_SAMPLE_RATE) / 100.0

        val enableRemoteConfig = if(!remoteConfigJson.has(ENABLE_REMOTE_CONFIG)) {
            remoteConfigJson.getBoolean(ENABLE_REMOTE_CONFIG)
        } else {
            false
        }

        return BTTRemoteConfiguration(
            networkSampleRate,
            enableRemoteConfig
        )
    }
}