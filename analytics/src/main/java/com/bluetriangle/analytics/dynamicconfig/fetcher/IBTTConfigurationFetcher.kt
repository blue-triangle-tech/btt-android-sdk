/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.fetcher

import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration

internal interface IBTTConfigurationFetcher {
    @Throws
    suspend fun fetch():BTTRemoteConfiguration
}