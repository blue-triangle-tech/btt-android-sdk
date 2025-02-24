package com.bluetriangle.analytics.thirdpartyintegration

internal interface ThirdPartyConnector {

    fun start()

    fun stop()

    fun setConfiguration(sdkConfiguration: SDKConfiguration)

    fun payloadFields():Map<String, String?>

}