package com.bluetriangle.analytics.launchtime

import com.bluetriangle.analytics.launchtime.model.LaunchEvent
import kotlinx.coroutines.channels.ReceiveChannel

internal interface LaunchEventProducer {

    val launchEvents: ReceiveChannel<LaunchEvent>

}