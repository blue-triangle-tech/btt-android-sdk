/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.launchtime.AppEventConsumer
import java.lang.Exception

internal class BTTConfigurationUpdater(
    private val repository: IBTTConfigurationRepository,
    private val fetcher: IBTTConfigurationFetcher,
    private val configRefreshDuration: Long,
) : IBTTConfigurationUpdater, AppEventConsumer {

    override suspend fun update() {
        val savedRemoteConfig = repository.get()
        val currentTime = System.currentTimeMillis()
        if (savedRemoteConfig == null || currentTime - savedRemoteConfig.savedDate > configRefreshDuration) {
            forceUpdate()
        }
    }

    override suspend fun forceUpdate() {
        try {
            val remoteConfig = fetcher.fetch()
            Tracker.instance?.configuration?.logger?.debug("Fetched remote config: $remoteConfig")
            repository.save(
                BTTSavedRemoteConfiguration(
                    remoteConfig.networkSampleRate,
                    System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Tracker.instance?.configuration?.logger?.error(e.message?:"Unknown error while fetching remote configuration")
        }
    }

}