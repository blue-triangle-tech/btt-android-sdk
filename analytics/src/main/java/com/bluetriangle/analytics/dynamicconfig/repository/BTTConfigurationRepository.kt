package com.bluetriangle.analytics.dynamicconfig.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject

internal class BTTConfigurationRepository(val context: Context, tag: String = "Default"):
    IBTTConfigurationRepository {

    companion object {
        private const val SAVED_CONFIG_PREFS = "SAVED_CONFIG"
        private const val REMOTE_CONFIG = "com.bluetriangle.analytics.REMOTE_CONFIG"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "${SAVED_CONFIG_PREFS}_$tag", Context.MODE_PRIVATE)

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
        if(System.getProperty("debug.btt.app.networksamplerate") == context.packageName) {
            return null
        }
        val savedConfigJson = sharedPreferences.getString(REMOTE_CONFIG, null)?:return null

        return BTTSavedRemoteConfiguration.fromJson(JSONObject(savedConfigJson))
    }

    override fun getLiveUpdates(): Flow<BTTSavedRemoteConfiguration?> = callbackFlow {
        val prefsChangeListener = OnSharedPreferenceChangeListener { prefs, s ->
            if(s == REMOTE_CONFIG) {
                trySend(get())
            }
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefsChangeListener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(prefsChangeListener)
        }
    }

}