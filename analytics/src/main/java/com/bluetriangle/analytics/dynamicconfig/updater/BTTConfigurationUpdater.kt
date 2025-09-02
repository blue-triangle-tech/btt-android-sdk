/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.appeventhub.AppEventConsumer
import com.bluetriangle.analytics.dynamicconfig.fetcher.BTTConfigFetchResult
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigUpdateReporter
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository

internal class BTTConfigurationUpdater(
    private val logger: Logger?,
    private val repository: IBTTConfigurationRepository,
    private val fetcher: IBTTConfigurationFetcher,
    private val configRefreshDuration: Long,
    private val reporter: BTTConfigUpdateReporter,
) : IBTTConfigurationUpdater, AppEventConsumer {

    override suspend fun update() {
        val savedRemoteConfig = repository.get()
        val currentTime = System.currentTimeMillis()
        if (currentTime - savedRemoteConfig.savedDate > configRefreshDuration) {
            forceUpdate()
        }
    }

    private var isFetching = false

    override suspend fun forceUpdate() {
        if (isFetching) return
        isFetching = true
        when (val result = fetcher.fetch()) {
            is BTTConfigFetchResult.Success -> {
                logger?.debug("Fetched remote config: ${result.config}")
                val savedRemoteConfig = repository.get()
                repository.save(result.config)
                if (result.config.enableRemoteConfigAck) {
                    if (result.config != savedRemoteConfig) {
                        reporter.reportSuccess()
                    }
                }
            }

            is BTTConfigFetchResult.Failure -> {
                val savedRemoteConfig = repository.get()
                logger?.error("Error while fetching remote config, Reason: ${result.error.reason}")
                if (savedRemoteConfig.enableRemoteConfigAck) {
                    reporter.reportError(result.error)
                }
            }
        }
        isFetching = false
    }

}