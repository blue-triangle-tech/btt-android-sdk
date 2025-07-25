/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfigurationMapper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject

internal class BTTConfigurationRepository(
    private val logger: Logger?,
    context: Context,
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
            config.ignoreScreens,
            config.enableRemoteConfigAck,
            config.enableAllTracking,
            config.enableScreenTracking,
            System.currentTimeMillis()
        )

        sharedPreferences.edit()
            .putString(configKey, BTTSavedRemoteConfigurationMapper.toJSONObject(savedConfig).toString())
            .apply()
    }

    private val configKey: String = "${REMOTE_CONFIG}_$siteId"

    override fun get(): BTTSavedRemoteConfiguration {
        val savedConfigJson = sharedPreferences.getString(configKey, null)?:return defaultConfig

        return try {
            BTTSavedRemoteConfigurationMapper.fromJson(JSONObject(savedConfigJson))
        } catch (e: Exception) {
            logger?.error("Error while parsing config JSON: ${e.message}")
            defaultConfig
        }
    }

    override fun getLiveUpdates(notifyCurrent: Boolean): Flow<BTTSavedRemoteConfiguration> = callbackFlow {
        if(notifyCurrent) {
            trySend(get())
        }

        val prefsChangeListener = OnSharedPreferenceChangeListener { _, s ->
            if(s == configKey) {
                trySend(get())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsChangeListener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsChangeListener)
        }
    }.buffer(Channel.UNLIMITED)

}