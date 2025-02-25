package com.bluetriangle.analytics.thirdpartyintegration

internal interface ThirdPartyConnector {

    fun start()

    fun stop()

    fun setConfiguration(connectorConfiguration: ConnectorConfiguration)

    fun payloadFields():Map<String, String?>

}