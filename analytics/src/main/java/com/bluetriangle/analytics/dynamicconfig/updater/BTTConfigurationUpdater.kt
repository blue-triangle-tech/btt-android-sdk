/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.dynamicconfig.fetcher.BTTConfigFetchResult
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigUpdateReporter
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class BTTConfigurationUpdater(
    private val repository: IBTTConfigurationRepository,
    private val fetcher: IBTTConfigurationFetcher,
    private val configRefreshDuration: Long,
    private val reporter: BTTConfigUpdateReporter,
) : IBTTConfigurationUpdater, AppEventConsumer {

    override suspend fun update() {
        val savedRemoteConfig = repository.get()
        val currentTime = System.currentTimeMillis()
        if (savedRemoteConfig == null || currentTime - savedRemoteConfig.savedDate > configRefreshDuration) {
            forceUpdate()
        }
    }

    override suspend fun forceUpdate() {
        when (val result = fetcher.fetch()) {
            is BTTConfigFetchResult.Success -> {
                Tracker.instance?.configuration?.logger?.debug("Fetched remote config: ${result.config}")
                val savedRemoteConfig = repository.get()
                repository.save(
                    BTTSavedRemoteConfiguration(
                        result.config.networkSampleRate,
                        result.config.enableRemoteConfigAck,
                        System.currentTimeMillis()
                    )
                )
                if(result.config.enableRemoteConfigAck && result.config != savedRemoteConfig) {
                    reporter.reportSuccess()
                }
            }

            is BTTConfigFetchResult.Failure -> {
                Tracker.instance?.configuration?.logger?.error("Error while fetching remote config, Reason: ${result.error.reason}")
                reporter.reportError(result.error)
            }
        }
    }

}