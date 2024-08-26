package com.bluetriangle.analytics.sessionmanager

interface SessionStore {

    fun storeSessionData(sessionData: SessionData)

    fun retrieveSessionData():SessionData?

}