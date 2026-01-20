package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.eventhub.AppEventConsumer

internal interface ISessionManager: AppEventConsumer {
    fun endSession()
    val sessionData: SessionData
}