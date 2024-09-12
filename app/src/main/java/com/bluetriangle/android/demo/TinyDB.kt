package com.bluetriangle.android.demo

import android.content.Context
import android.content.SharedPreferences

class TinyDB(context: Context) {
    private var sharedPref: SharedPreferences =
        context.getSharedPreferences("BlueTriangleDemo", Context.MODE_PRIVATE)

    fun getString(key: String): String? {
        return sharedPref.getString(key, "")
    }

    fun getString(key: String, default: String?): String? {
        return sharedPref.getString(key, default)
    }

    fun setString(key: String, value: String?) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return sharedPref.getBoolean(key, default)
    }

    fun setBoolean(key: String, value: Boolean) {
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun getInt(key: String, default: Int): Int {
        return sharedPref.getInt(key, default)
    }

    fun setInt(key: String, value: Int) {
        val editor = sharedPref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getFloat(key: String): Float {
        return sharedPref.getFloat(key, 0f)
    }

    fun getFloat(key: String, default: Float): Float {
        return sharedPref.getFloat(key, default)
    }

    fun setFloat(key: String, value: Float) {
        val editor = sharedPref.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getLong(key: String): Long {
        return sharedPref.getLong(key, 0L)
    }

    fun getLong(key: String, default: Long): Long {
        return sharedPref.getLong(key, default)
    }

    fun setLong(key: String, value: Long) {
        val editor = sharedPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun removeAll() {
        sharedPref.edit().clear().apply()
    }

    fun remove(key: String?) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }

    fun contains(key:String?): Boolean {
        return sharedPref.contains(key)
    }
}
