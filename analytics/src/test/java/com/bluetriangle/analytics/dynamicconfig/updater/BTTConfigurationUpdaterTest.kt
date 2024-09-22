package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.dynamicconfig.Configurationhandler
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BTTConfigurationUpdaterTest {

    @Mock
    private lateinit var fetcher: IBTTConfigurationFetcher

    @Mock
    private lateinit var repository: IBTTConfigurationRepository

    @Mock
    private lateinit var sampleRateHandler: Configurationhandler

    private lateinit var updater: BTTConfigurationUpdater

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        updater = BTTConfigurationUpdater(repository, fetcher, sampleRateHandler, 200)
    }

    @Test
    fun `When update is called before anything is stored in cache should fetch configuration from API`() {
        runBlocking {
            whenever(repository.get()).thenAnswer {
                BTTSavedRemoteConfiguration(0.05, 0)
            }
            whenever(fetcher.fetch()).thenAnswer {
                BTTRemoteConfiguration(1.0)
            }
            updater.update()
            verify(fetcher).fetch()
        }
    }

    @Test
    fun `When update is called after cache refresh duration should fetch new configuration from API`() {
        runBlocking {
            val sampleRatePercent = Math.random()
            whenever(fetcher.fetch()).thenReturn(BTTRemoteConfiguration(sampleRatePercent))
            whenever(repository.get()).thenReturn(BTTSavedRemoteConfiguration(sampleRatePercent, System.currentTimeMillis()))
            Thread.sleep(210)
            updater.update()
            verify(fetcher).fetch()
        }
    }

    @Test
    fun `when new data is received from API should update NetworkSampleRateHandler`() {
        runBlocking {
            val apiSampleRate = 0.75
            val savedSampleRate = 0.25
            whenever(fetcher.fetch()).thenReturn(BTTRemoteConfiguration(apiSampleRate))
            var savedRemoteConfig = BTTSavedRemoteConfiguration(savedSampleRate, System.currentTimeMillis())
            whenever(repository.get()).thenAnswer {
                savedRemoteConfig
            }
            whenever(repository.save(any())).thenAnswer {
                savedRemoteConfig = (it.arguments[0] as BTTSavedRemoteConfiguration)
                Unit
            }
            Thread.sleep(210)
            updater.update()
            verify(sampleRateHandler).updateNetworkSampleRate(apiSampleRate)
        }
    }
}