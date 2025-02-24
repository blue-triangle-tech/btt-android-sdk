package com.bluetriangle.analytics.thirdpartyintegration

internal class ThirdPartyConnectorManager {
    private var connectors = mutableListOf<ThirdPartyConnector>()

    val payloadFields: Map<String, String?>
        get() = buildMap {
            connectors.forEach {
                putAll(it.payloadFields())
            }
        }

    fun register(connector: ThirdPartyConnector) {
        connectors.add(connector)
    }

    fun startConnectors() {
        connectors.forEach { it.start() }
    }

    fun setConfiguration(config: SDKConfiguration) {
        connectors.forEach {
            it.setConfiguration(config)
        }
    }

    fun stopConnectors() {
        connectors.forEach { it.stop() }
    }

}