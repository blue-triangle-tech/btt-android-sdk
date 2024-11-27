package com.bluetriangle.analytics.dynamicconfig.model

import com.bluetriangle.analytics.utility.getDoubleOrNull
import org.json.JSONObject

internal object BTTSavedRemoteConfigurationMapper {
    private const val NETWORK_SAMPLE_RATE = "networkSampleRateSDK"
    private const val ENABLE_REMOTE_CONFIG = "enableRemoteConfigAck"
    private const val SAVED_DATE = "savedDate"

    fun fromJson(jsonObject: JSONObject): BTTSavedRemoteConfiguration {
        return BTTSavedRemoteConfiguration(
            jsonObject.getDoubleOrNull(NETWORK_SAMPLE_RATE),
            jsonObject.getBoolean(ENABLE_REMOTE_CONFIG),
            jsonObject.getLong(SAVED_DATE)
        )
    }

    fun toJSONObject(config: BTTSavedRemoteConfiguration) = JSONObject().apply {
        put(NETWORK_SAMPLE_RATE, config.networkSampleRate)
        put(ENABLE_REMOTE_CONFIG, config.enableRemoteConfigAck)
        put(SAVED_DATE, config.savedDate)
    }
}