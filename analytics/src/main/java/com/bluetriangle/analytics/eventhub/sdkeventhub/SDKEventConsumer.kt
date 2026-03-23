package com.bluetriangle.analytics.eventhub.sdkeventhub

import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.launchtime.model.LaunchType
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkstate.BTTNetworkState

internal interface SDKEventConsumer

internal interface TimerEventConsumer : SDKEventConsumer {
    fun onTimerStarted(timer: Timer) {}
    fun onTimerSubmitted(timer: Timer) {}
}

internal interface NetworkEventConsumer : SDKEventConsumer {
    fun onNetworkRequestCaptured(networkRequest: CapturedRequest) {}
    fun onNetworkStateChanged(networkState: BTTNetworkState) {}
}

internal interface AppLifecycleEventConsumer : SDKEventConsumer {
    fun onLaunchDetected(launchType: LaunchType) {}
    fun onAppInstall(appVersion: String) {}
    fun onAppUpdate(oldVersion: String, newVersion: String) {}
}