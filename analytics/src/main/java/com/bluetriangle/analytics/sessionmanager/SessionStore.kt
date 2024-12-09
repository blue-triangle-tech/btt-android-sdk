/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.sessionmanager

internal interface SessionStore {

    fun storeSessionData(sessionData: SessionData)

    fun retrieveSessionData():SessionData?

}