package com.bluetriangle.analytics.thirdpartyintegration

class ThirdPartyConnectionManager {

    companion object {
        internal val connectors = arrayListOf<ThirdPartyConnector>()

        fun register(connector: ThirdPartyConnector) {
            connectors.add(connector)
        }
    }

    fun start() {
        connectors.forEach {
            it.start()
        }
    }

    fun stop() {
        connectors.forEach {
            it.stop()
        }
    }

    fun getPayload() {
        connectors.forEach {

        }
    }
}