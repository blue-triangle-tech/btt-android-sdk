package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class BTTConfigurationUpdater(
    private val repository: IBTTConfigurationRepository,
    private val fetcher: IBTTConfigurationFetcher,
    private val configRefreshDuration: Long,
) : IBTTConfigurationUpdater, AppEventConsumer {

    override suspend fun update() {
        val savedRemoteConfig = repository.get()
        val currentTime = System.currentTimeMillis()
        if (savedRemoteConfig == null || currentTime - savedRemoteConfig.savedDate > configRefreshDuration) {
            val remoteConfig = fetcher.fetch()
            repository.save(
                BTTSavedRemoteConfiguration(
                    remoteConfig.networkSampleRate,
                    currentTime
                )
            )
        }
    }

}