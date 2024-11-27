package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.utility.getBooleanOrNull
import org.json.JSONObject

internal object BTTRemoteConfigurationMapper {

    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"

    fun fromJson(remoteConfigJson: JSONObject): BTTRemoteConfiguration {
        if (!remoteConfigJson.has(NETWORK_SAMPLE_RATE)) {
            throw MissingFieldException(arrayOf(NETWORK_SAMPLE_RATE))
        }
        val networkSampleRate = remoteConfigJson.getInt(NETWORK_SAMPLE_RATE) / 100.0

        val enableRemoteConfig = remoteConfigJson.getBooleanOrNull(ENABLE_REMOTE_CONFIG)?:false

        return BTTRemoteConfiguration(
            networkSampleRate,
            enableRemoteConfig
        )
    }
}