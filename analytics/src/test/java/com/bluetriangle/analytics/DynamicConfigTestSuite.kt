package com.bluetriangle.analytics

import com.bluetriangle.analytics.dynamicconfig.fetcher.BTTConfigurationFetcherTest
import com.bluetriangle.analytics.dynamicconfig.repository.BTTConfigurationRepositoryTest
import com.bluetriangle.analytics.dynamicconfig.updater.BTTConfigurationUpdaterTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    BTTConfigurationFetcherTest::class,
    BTTConfigurationRepositoryTest::class,
    BTTConfigurationUpdaterTest::class
)
class DynamicConfigTestSuite