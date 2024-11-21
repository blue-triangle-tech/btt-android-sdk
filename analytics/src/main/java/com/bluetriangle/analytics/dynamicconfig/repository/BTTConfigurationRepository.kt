/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject

internal class BTTConfigurationRepository(context: Context,
                                          siteId: String,
                                          private val defaultConfig: BTTSavedRemoteConfiguration):
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
            .putString(configKey, savedConfig.toJSONObject().toString())
            .apply()
    }

    private val configKey: String = "${REMOTE_CONFIG}_$siteId"

    override fun get(): BTTSavedRemoteConfiguration {
        val savedConfigJson = sharedPreferences.getString(configKey, null)?:return defaultConfig

        return try {
            BTTSavedRemoteConfiguration.fromJson(JSONObject(savedConfigJson))
        } catch (e: Exception) {
            Tracker.instance?.configuration?.logger?.error("Error while parsing config JSON: ${e.message}")
            defaultConfig
        }
    }

    override fun getLiveUpdates(): Flow<BTTSavedRemoteConfiguration> = callbackFlow {
        trySendBlocking(get())

        val prefsChangeListener = OnSharedPreferenceChangeListener { _, s ->
            if(s == REMOTE_CONFIG) {
                trySendBlocking(get())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsChangeListener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsChangeListener)
        }
    }

}