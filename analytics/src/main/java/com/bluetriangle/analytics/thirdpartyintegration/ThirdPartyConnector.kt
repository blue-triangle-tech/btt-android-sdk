package com.bluetriangle.analytics.thirdpartyintegration

internal interface ThirdPartyConnector {

    fun start()

    fun stop()

    fun setConfiguration(connectorConfiguration: ConnectorConfiguration)

    fun nativeAppPayloadFields(): Map<String, String?>

    fun payloadFields():Map<String, String?>

}