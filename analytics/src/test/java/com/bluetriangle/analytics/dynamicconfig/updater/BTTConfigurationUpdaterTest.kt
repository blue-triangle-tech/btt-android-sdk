package com.bluetriangle.analytics.dynamicconfig.updater

import com.bluetriangle.analytics.BlueTriangleConfiguration
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.deviceinfo.DeviceInfoProvider
import com.bluetriangle.analytics.dynamicconfig.fetcher.BTTConfigFetchResult
import com.bluetriangle.analytics.dynamicconfig.fetcher.IBTTConfigurationFetcher
import com.bluetriangle.analytics.dynamicconfig.model.BTTRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.model.BTTSavedRemoteConfiguration
import com.bluetriangle.analytics.dynamicconfig.reporter.BTTConfigUpdateReporter
import com.bluetriangle.analytics.dynamicconfig.repository.IBTTConfigurationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BTTConfigurationUpdaterTest {

    @Mock
    private lateinit var fetcher: IBTTConfigurationFetcher

    @Mock
    private lateinit var repository: IBTTConfigurationRepository

    private lateinit var updater: BTTConfigurationUpdater

    @Mock
    private lateinit var mockLogger: Logger

    @Mock
    private lateinit var configuration: BlueTriangleConfiguration

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        updater = BTTConfigurationUpdater(
            mockLogger, repository, fetcher, 200, BTTConfigUpdateReporter(
                configuration,
                DeviceInfoProvider
            )
        )
    }

    @Test
    fun `When update is called before anything is stored in cache should fetch configuration from API`() {
        runBlocking {
            whenever(repository.get()).thenAnswer {
                BTTSavedRemoteConfiguration(0.05, emptyList(), true, true, true, true, 2, 0.05, 0)
            }
            whenever(fetcher.fetch()).thenAnswer {
                BTTConfigFetchResult.Success(BTTRemoteConfiguration(1.0, emptyList(), true, true, true, true, 2, 0.05))
            }
            updater.update()
            verify(fetcher).fetch()
        }
    }

    @Test
    fun `When update is called after cache refresh duration should fetch new configuration from API`() {
        runBlocking {
            val sampleRatePercent = Math.random()
            whenever(fetcher.fetch()).thenReturn(BTTConfigFetchResult.Success(BTTRemoteConfiguration(sampleRatePercent, emptyList(), true, true, true, true, 2, 0.05)))
            whenever(repository.get()).thenReturn(BTTSavedRemoteConfiguration(sampleRatePercent, emptyList(), true, true, true, true, 2, sampleRatePercent, System.currentTimeMillis()))
            Thread.sleep(210)
            updater.update()
            verify(fetcher).fetch()
        }
    }
}