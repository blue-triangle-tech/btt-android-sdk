package com.bluetriangle.analytics.dynamicconfig

internal interface ConfigurationUpdatesListener {

    fun onShouldSampleNetworkChanged(shouldSampleNetwork: Boolean)

    fun onSessionIDChanged(sessionID:String)

}