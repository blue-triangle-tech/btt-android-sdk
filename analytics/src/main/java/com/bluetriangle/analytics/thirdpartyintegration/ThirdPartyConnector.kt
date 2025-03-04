package com.bluetriangle.analytics.thirdpartyintegration

import com.bluetriangle.analytics.Logger

internal abstract class ThirdPartyConnector(protected val logger: Logger?, protected val customVariablesAdapter: CustomVariablesAdapter) {

    abstract fun start(connectorConfiguration: ConnectorConfiguration)

    abstract fun stop()

    abstract fun nativeAppPayloadFields(): Map<String, String?>

    abstract fun payloadFields():Map<String, String?>

}