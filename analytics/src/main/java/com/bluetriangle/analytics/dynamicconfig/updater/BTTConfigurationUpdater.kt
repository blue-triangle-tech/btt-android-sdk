package com.bluetriangle.analytics.dynamicconfig.updater

import android.util.Log
import com.bluetriangle.analytics.dynamicconfig.Configurationhandler
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class BTTConfigurationUpdater(
    private val repository: IBTTConfigurationRepository,
    private val fetcher: IBTTConfigurationFetcher,
    private val configurationhandler: Configurationhandler,
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

            if(savedRemoteConfig == null || savedRemoteConfig.networkSampleRate != remoteConfig.networkSampleRate) {
                configurationhandler.updateNetworkSampleRate(remoteConfig.networkSampleRate)
            }
        }
    }

}