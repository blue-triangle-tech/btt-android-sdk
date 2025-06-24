package com.bluetriangle.android.demo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.key
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class SharedPreferencesMgr(context: Context) {
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences("BlueTriangleDemo", Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return sharedPref.getString(key, "")
    }

    fun setString(key: String, value: String?) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun setBoolean(key: String, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun getInt(key: String, default:Int): Int {
        return sharedPref.getInt(key, default)
    }

    fun setInt(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun removeAll() {
        sharedPref.edit().clear().apply()
    }

    fun remove(key: String?) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun observeBoolean(key: String) = callbackFlow{
        trySend(sharedPref.getBoolean(key, false))
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { pref, k ->
            if(k == key) {
                trySend(pref.getBoolean(key, false))
            }
        }
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
        return@callbackFlow awaitClose {
            sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}
