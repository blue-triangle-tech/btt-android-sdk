package com.bluetriangle.analytics.sessionmanager

internal interface SessionStore {

    fun storeSessionData(sessionData: SessionData)

    fun retrieveSessionData():SessionData?

}