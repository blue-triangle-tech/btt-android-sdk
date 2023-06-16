package com.bluetriangle.analytics.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class NativeAppProperties(
    val loadTime:Long,
    val fullTime:Long,
    val maxCPUUses:Long,
    val viewType:ViewType
):Parcelable