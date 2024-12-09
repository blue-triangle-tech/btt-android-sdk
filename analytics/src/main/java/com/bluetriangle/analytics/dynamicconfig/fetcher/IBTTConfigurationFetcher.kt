/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.fetcher

internal interface IBTTConfigurationFetcher {
    @Throws
    suspend fun fetch():BTTConfigFetchResult
}