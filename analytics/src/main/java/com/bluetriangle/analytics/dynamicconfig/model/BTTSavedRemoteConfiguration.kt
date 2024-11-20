/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.model

import org.json.JSONObject

internal class BTTSavedRemoteConfiguration(networkSampleRate: Double, val savedDate: Long) :
    BTTRemoteConfiguration(networkSampleRate) {

    companion object {
        fun fromJson(jsonObject: JSONObject): BTTSavedRemoteConfiguration {
            return BTTSavedRemoteConfiguration(
                jsonObject.getDouble(NETWORK_SAMPLE_RATE),
                jsonObject.getLong(SAVED_DATE)
            )
        }

        private const val NETWORK_SAMPLE_RATE = "networkSampleRate"
        private const val SAVED_DATE = "savedDate"
    }

    fun toJSONObject() = JSONObject().apply {
        put(NETWORK_SAMPLE_RATE, networkSampleRate)
        put(SAVED_DATE, savedDate)
    }

}
