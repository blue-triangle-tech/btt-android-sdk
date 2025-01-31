/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.repository

import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import kotlinx.coroutines.flow.Flow

internal interface IBTTConfigurationRepository {
    fun save(config: BTTRemoteConfiguration)
    fun get(): BTTSavedRemoteConfiguration
    fun getLiveUpdates(notifyCurrent: Boolean): Flow<BTTSavedRemoteConfiguration?>
}