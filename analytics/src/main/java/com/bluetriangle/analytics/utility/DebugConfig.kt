/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.utility

import com.bluetriangle.analytics.BuildConfig
import com.bluetriangle.analytics.Tracker
import java.io.BufferedReader
import java.io.InputStreamReader

class DebugConfig private constructor(
    val fullSampleRate: Boolean,
    val newSessionOnLaunch: Boolean,
    val configUrl: String?
) {
    companion object {
        private val isDebugMode: Boolean
            get() = System.getProperty("android.debug.process") == "true"

        val current: DebugConfig
            get() = if (isDebugMode) {
                DebugConfig(
                    getShellProperty("debug.full.sample.rate") == "on",
                    getShellProperty("debug.new.session.on.launch") == "on",
                    getShellProperty("debug.config.url")
                ).also {
                    Tracker.instance?.configuration?.logger?.debug("In Debug Mode, DebugConfig $it")
                }
            } else {
                DebugConfig(
                    fullSampleRate = false,
                    newSessionOnLaunch = false,
                    null
                ).also {
                    Tracker.instance?.configuration?.logger?.debug("Not in Debug Mode, DebugConfig $it")
                }
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