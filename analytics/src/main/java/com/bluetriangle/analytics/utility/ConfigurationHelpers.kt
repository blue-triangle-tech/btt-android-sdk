package com.bluetriangle.analytics.utility

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build

class ConfigurationChange(
    val fieldName: String,
    val from: String,
    val to: String
) {
    override fun toString(): String {
        return """${fieldName}: $from → $to"""
    }
}

fun getConfigurationChanges(
    oldConfig: Configuration,
    newConfig: Configuration
): List<ConfigurationChange> {

    val changes = mutableListOf<ConfigurationChange>()
    val diff = oldConfig.diff(newConfig)

    fun addChange(
        field: String,
        oldValue: String,
        newValue: String
    ) {
        changes += ConfigurationChange(field, oldValue, newValue)
    }

    var isOrientationChanged = false

    if ((diff and ActivityInfo.CONFIG_ORIENTATION) != 0) {
        isOrientationChanged = true
        addChange(
            "orientation",
            oldConfig.orientationToString(),
            newConfig.orientationToString()
        )
    }

    if (diff and ActivityInfo.CONFIG_LOCALE != 0) {
        addChange(
            "locale",
            oldConfig.getPrimaryLocale(),
            newConfig.getPrimaryLocale()
        )
    }

    if (diff and ActivityInfo.CONFIG_UI_MODE != 0) {
        addChange(
            "uiMode",
            oldConfig.uiModeToString(),
            newConfig.uiModeToString()
        )
    }

    if (diff and ActivityInfo.CONFIG_SCREEN_SIZE != 0 && !isOrientationChanged) {
        addChange(
            "screenWidthDp",
            oldConfig.screenWidthDp.toString(),
            newConfig.screenWidthDp.toString()
        )

        addChange(
            "screenHeightDp",
            oldConfig.screenHeightDp.toString(),
            newConfig.screenHeightDp.toString()
        )
    }

    if (diff and ActivityInfo.CONFIG_DENSITY != 0) {
        addChange(
            "densityDpi",
            oldConfig.densityDpi.toString(),
            newConfig.densityDpi.toString()
        )
    }

    if (diff and ActivityInfo.CONFIG_FONT_SCALE != 0) {
        addChange(
            "fontScale",
            oldConfig.fontScale.toString(),
            newConfig.fontScale.toString()
        )
    }

    if (diff and ActivityInfo.CONFIG_LAYOUT_DIRECTION != 0) {
        addChange(
            "layoutDirection",
            oldConfig.layoutDirectionToString(),
            newConfig.layoutDirectionToString()
        )
    }

    return changes
}

private fun Configuration.getPrimaryLocale(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)?.toLanguageTag() ?: "unknown"
    } else {
        @Suppress("DEPRECATION")
        locale?.toLanguageTag() ?: "unknown"
    }
}

fun Configuration.orientationToString(): String =
    when (orientation) {
        Configuration.ORIENTATION_PORTRAIT -> "portrait"
        Configuration.ORIENTATION_LANDSCAPE -> "landscape"
        else -> "undefined"
    }

private fun Configuration.layoutDirectionToString(): String =
    when (layoutDirection) {
        Configuration.SCREENLAYOUT_LAYOUTDIR_LTR -> "ltr"
        Configuration.SCREENLAYOUT_LAYOUTDIR_RTL -> "rtl"
        else -> "undefined"
    }

private fun Configuration.uiModeToString(): String {
    val nightMode = when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> "night"
        Configuration.UI_MODE_NIGHT_NO -> "not_night"
        else -> "undefined"
    }

    val type = when (uiMode and Configuration.UI_MODE_TYPE_MASK) {
        Configuration.UI_MODE_TYPE_NORMAL -> "normal"
        Configuration.UI_MODE_TYPE_DESK -> "desk"
        Configuration.UI_MODE_TYPE_CAR -> "car"
        Configuration.UI_MODE_TYPE_TELEVISION -> "tv"
        Configuration.UI_MODE_TYPE_WATCH -> "watch"
        Configuration.UI_MODE_TYPE_VR_HEADSET -> "vr"
        else -> "undefined"
    }

    return "$type|$nightMode"
}