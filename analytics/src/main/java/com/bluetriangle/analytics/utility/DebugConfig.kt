/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.utility

import android.content.Context
import android.util.Log
import com.bluetriangle.analytics.BuildConfig
import com.bluetriangle.analytics.Tracker
import java.io.BufferedReader
import java.io.InputStreamReader

internal class DebugConfig private constructor(
    val fullSampleRate: Boolean,
    val newSessionOnLaunch: Boolean
) {
    companion object {

        fun getCurrent(context: Context): DebugConfig = if (context.isDebugBuild) {
            DebugConfig(
                getShellProperty("debug.full.sample.rate") == "on",
                getShellProperty("debug.new.session.on.launch") == "on"
            )
        } else {
            DebugConfig(
                fullSampleRate = false,
                newSessionOnLaunch = false
            )
        }

        private fun getShellProperty(propertyName: String): String? {
            return try {
                val process = Runtime.getRuntime().exec("getprop $propertyName")
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    reader.readLine().ifBlank { null }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}