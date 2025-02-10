package com.bluetriangle.analytics.sessionmanager

import com.bluetriangle.analytics.appeventhub.AppEventConsumer

internal interface ISessionManager: AppEventConsumer {
    fun endSession()
    val sessionData: SessionData
}