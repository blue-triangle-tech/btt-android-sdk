package com.bluetriangle.analytics.thirdpartyintegration

interface ThirdPartyConnector {

    fun start()

    fun stop()

    fun configuration()
    fun payloadFields():Map<String, Any>

}