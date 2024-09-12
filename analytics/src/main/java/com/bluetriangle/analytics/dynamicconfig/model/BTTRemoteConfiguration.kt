package com.bluetriangle.analytics.dynamicconfig.model

import org.json.JSONObject

internal open class BTTRemoteConfiguration(
    val networkSampleRate: Double
) {
    companion object {
        fun fromJson(remoteConfigJson:JSONObject) = BTTRemoteConfiguration(
            remoteConfigJson.getInt("wcdSamplePercent")/100.0
        )
    }
}