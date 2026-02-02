package com.bluetriangle.analytics.screenTracking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class EventID(val id: Int): Parcelable {
    object ColdLaunch: EventID(1)
    object WarmLaunch: EventID(2)
    object HotLaunch: EventID(3)
    object ANRWarning: EventID(4)
    object MemoryWarning: EventID(5)
    object Crash: EventID(7)
}