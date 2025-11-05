package com.bluetriangle.analytics.screenTracking

import android.view.Window
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import junit.framework.TestCase.fail
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ActivityLifecycleTrackerTest {

    private val mockWindow = mock(Window::class.java)
    private val mockFragmentManager = mock(FragmentManager::class.java)

    init {
        val callback = mock(Window.Callback::class.java)
        `when`(mockWindow.callback).thenReturn(callback)
    }

    private fun getActivityLifecycleTracker(): ActivityLifecycleTracker {
        val screenTracker = mock(ScreenLifecycleTracker::class.java)
        val fragmentTracker = mock(FragmentLifecycleTracker::class.java)
        return ActivityLifecycleTracker(screenTracker, fragmentTracker)
    }

    private fun mockActivity() = mock(FragmentActivity::class.java).apply {
        `when`(window).thenReturn(mockWindow)
        `when`(supportFragmentManager).thenReturn(mockFragmentManager)
    }

    @Test
    fun `when onActivityDestroyed called while unregister is executing should not throw ConcurrentModificationException`() {
        addActivitiesAndCallBlockWhileUnregister {
            activities, activityLifecycleTracker ->
            activityLifecycleTracker.onActivityDestroyed(activities.random())
        }
    }

    @Test
    fun `when onActivityCreated called while unregister is executing should not throw ConcurrentModificationException`() {
        addActivitiesAndCallBlockWhileUnregister { _, activityLifecycleTracker ->
            activityLifecycleTracker.onActivityCreated(mockActivity(), null)
        }
    }

    private fun addActivitiesAndCallBlockWhileUnregister(block: (List<FragmentActivity>, ActivityLifecycleTracker) -> Unit) {
        val activityLifecycleTracker = getActivityLifecycleTracker()
        // Add many activities to increase iteration time
        val activities = List(500) { mockActivity() }
        activities.forEach {
            activityLifecycleTracker.onActivityCreated(it, null)
        }

        val destroyThread = Thread {
            repeat(100) {
                block(activities, activityLifecycleTracker)
            }
        }

        destroyThread.start()
        // While other thread removes, we iterate
        try {
            activityLifecycleTracker.unregister()
        } catch (e: ConcurrentModificationException) {
            fail("Should not throw ConcurrentModificationException")
        }

        destroyThread.join()
    }
}