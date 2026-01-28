package com.bluetriangle.analytics.eventhub

import com.bluetriangle.analytics.Timer

interface SDKEventConsumer {
    fun onTimerStarted(timer: Timer) {

    }
    fun onTimerSubmitted(timer: Timer) {

    }
}