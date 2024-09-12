package com.bluetriangle.android.demo

import android.content.Context
import android.content.SharedPreferences

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
}
