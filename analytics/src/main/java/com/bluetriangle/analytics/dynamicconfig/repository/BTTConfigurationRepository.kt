package com.bluetriangle.analytics.dynamicconfig.repository

import android.content.Context
import android.content.SharedPreferences
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import org.json.JSONObject

internal class BTTConfigurationRepository(context: Context):
    IBTTConfigurationRepository {

    companion object {
        private const val SAVED_CONFIG_PREFS = "SAVED_CONFIG"
        private const val REMOTE_CONFIG = "com.bluetriangle.analytics.REMOTE_CONFIG"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SAVED_CONFIG_PREFS, Context.MODE_PRIVATE)

    override fun save(config: BTTRemoteConfiguration) {
        val savedConfig = BTTSavedRemoteConfiguration(
            config.networkSampleRate,
            System.currentTimeMillis()
        )

        sharedPreferences.edit()
            .putString(REMOTE_CONFIG, savedConfig.toJSONObject().toString())
            .apply()
    }

    override fun get(): BTTSavedRemoteConfiguration? {
        val savedConfigJson = sharedPreferences.getString(REMOTE_CONFIG, null)?:return null

        return BTTSavedRemoteConfiguration.fromJson(JSONObject(savedConfigJson))
    }

}