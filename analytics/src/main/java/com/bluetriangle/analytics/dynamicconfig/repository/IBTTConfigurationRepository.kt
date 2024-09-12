package com.bluetriangle.analytics.dynamicconfig.repository

import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration

internal interface IBTTConfigurationRepository {
    fun save(config: BTTRemoteConfiguration)
    fun get(): BTTSavedRemoteConfiguration?
}