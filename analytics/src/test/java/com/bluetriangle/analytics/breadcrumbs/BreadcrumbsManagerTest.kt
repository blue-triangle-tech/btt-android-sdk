package com.bluetriangle.analytics.breadcrumbs

import com.bluetriangle.analytics.breadcrumbs.config.BreadcrumbsConfig
import com.bluetriangle.analytics.breadcrumbs.config.BreadcrumbsFeature
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppInstallInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppLifecycleInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.AppUpdateInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.NetworkRequestInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.NetworkStateInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.SystemEventsInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.UiLifecycleInstrumentation
import com.bluetriangle.analytics.breadcrumbs.instrumentation.UserEventInstrumentation
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Test

class BreadcrumbsManagerTest {

     private fun configWith(vararg active: BreadcrumbsFeature): BreadcrumbsConfig {
        val allFeatures = BreadcrumbsFeature.values().toSet()
        val ignored = allFeatures - active.toSet()
        return BreadcrumbsConfig(true, 100, ignoredFeatures = ignored.toList())
    }

    private fun configAllFeatures() = BreadcrumbsConfig(true, 100, ignoredFeatures = emptyList())

    private fun spyManagerWithMockedInstrumentations(config: BreadcrumbsConfig): BreadcrumbsManager {
        val manager = spyk(BreadcrumbsManager(config))
        every { manager.featureToInstrumentation(any(), any()) } answers {
            // Return a unique mock per invocation so each feature gets its own mock.
            mockk(relaxed = true)
        }
        return manager
    }

    @Test
    fun `install - all features enabled - creates instrumentation for every feature`() {
        val manager = spyManagerWithMockedInstrumentations(configAllFeatures())

        manager.install()

        // featureToInstrumentation should have been called once per feature
        BreadcrumbsFeature.values().forEach { feature ->
            verify(exactly = 1) { manager.featureToInstrumentation(feature, any()) }
        }
    }

    @Test
    fun `install - subset of features - only creates instrumentations for active features`() {
        val activeFeatures = setOf(BreadcrumbsFeature.AppLifecycle, BreadcrumbsFeature.NetworkState)
        val manager = spyManagerWithMockedInstrumentations(configWith(*activeFeatures.toTypedArray()))

        manager.install()

        activeFeatures.forEach { feature ->
            verify(exactly = 1) { manager.featureToInstrumentation(feature, any()) }
        }
        BreadcrumbsFeature.values().filterNot { it in activeFeatures }.forEach { feature ->
            verify(exactly = 0) { manager.featureToInstrumentation(feature, any()) }
        }
    }

    @Test
    fun `install - no features - creates no instrumentations`() {
        val manager = spyManagerWithMockedInstrumentations(
            BreadcrumbsConfig(true, 100, ignoredFeatures = BreadcrumbsFeature.values().toList())
        )

        manager.install()

        verify(exactly = 0) { manager.featureToInstrumentation(any(), any()) }
    }

    @Test
    fun `uninstall - calling install again after uninstall re-creates instrumentations`() {
        val manager = spyManagerWithMockedInstrumentations(configAllFeatures())

        manager.install()
        manager.uninstall()
        manager.install()

        // featureToInstrumentation should have been called twice per feature (once per install)
        BreadcrumbsFeature.values().forEach { feature ->
            verify(exactly = 2) { manager.featureToInstrumentation(feature, any()) }
        }
    }

    // ── updateConfig() ───────────────────────────────────────────────────────

    @Test
    fun `updateConfig - adding new features - creates and enables the new instrumentations`() {
        val manager = spyManagerWithMockedInstrumentations(
            configWith(BreadcrumbsFeature.AppLifecycle)
        )
        manager.install()
        verify(exactly = 0) { manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, any()) }
        verify(exactly = 1) { manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, any()) }

        clearMocks(manager, answers = false, recordedCalls = true)

        val newConfig = configWith(BreadcrumbsFeature.AppLifecycle, BreadcrumbsFeature.NetworkState)
        manager.updateConfig(newConfig)
        verify(exactly = 0) { manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, any()) }
        verify(exactly = 1) { manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, any()) }
    }

    @Test
    fun `updateConfig - removing features - does not create instrumentation for removed feature`() {
        val manager = spyManagerWithMockedInstrumentations(
            configWith(BreadcrumbsFeature.AppLifecycle, BreadcrumbsFeature.NetworkState)
        )
        manager.install()
        verify(exactly = 1) { manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, any()) }
        verify(exactly = 1) { manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, any()) }

        clearMocks(manager, answers = false, recordedCalls = true)

        val newConfig = configWith(BreadcrumbsFeature.AppLifecycle)
        manager.updateConfig(newConfig)

        verify(exactly = 0) { manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, any()) }
        verify(exactly = 0) { manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, any()) }
    }

    @Test
    fun `updateConfig - no change in features - does not create or remove any instrumentation`() {
        val manager = spyManagerWithMockedInstrumentations(
            configWith(BreadcrumbsFeature.AppLifecycle)
        )
        manager.install()
        verify(exactly = 1) { manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, any()) }

        clearMocks(manager, answers = false, recordedCalls = true)

        manager.updateConfig(configWith(BreadcrumbsFeature.AppLifecycle))
        verify(exactly = 0) { manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, any()) }

    }

    @Test
    fun `updateConfig - before install is called - does nothing`() {
        val manager = spyManagerWithMockedInstrumentations(configAllFeatures())

        manager.updateConfig(configAllFeatures())

        verify(exactly = 0) { manager.featureToInstrumentation(any(), any()) }
    }

    @Test
    fun `featureToInstrumentation - AppLifecycle - returns AppLifecycleInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        val instrumentation = manager.featureToInstrumentation(BreadcrumbsFeature.AppLifecycle, collector)

        assertTrue(instrumentation is AppLifecycleInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - UiLifecycle - returns UiLifecycleInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.UiLifecycle, collector) is UiLifecycleInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - NetworkRequest - returns NetworkRequestInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.NetworkRequest, collector) is NetworkRequestInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - NetworkState - returns NetworkStateInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.NetworkState, collector) is NetworkStateInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - AppInstall - returns AppInstallInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.AppInstall, collector) is AppInstallInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - AppUpdate - returns AppUpdateInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.AppUpdate, collector) is AppUpdateInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - UserEvent - returns UserEventInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.UserEvent, collector) is UserEventInstrumentation)
    }

    @Test
    fun `featureToInstrumentation - SystemEvent - returns SystemEventsInstrumentation`() {
        val manager = BreadcrumbsManager(configAllFeatures())
        val collector = mockk<BreadcrumbsCollector>(relaxed = true)

        assertTrue(manager.featureToInstrumentation(BreadcrumbsFeature.SystemEvent, collector) is SystemEventsInstrumentation)
    }

}