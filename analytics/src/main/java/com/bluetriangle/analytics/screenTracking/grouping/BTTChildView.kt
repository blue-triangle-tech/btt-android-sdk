package com.bluetriangle.analytics.screenTracking.grouping

import com.bluetriangle.analytics.model.NativeAppProperties

internal class BTTChildView(
    val className: String,
    val pageName: String,
    val pageTime: String,
    val startTime: String,
    val endTime: String,
    val nativeAppProperties: NativeAppProperties
) {
    companion object{
        const val ENTRY_TYPE = "screen"
    }
}