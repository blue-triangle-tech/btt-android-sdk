package com.bluetriangle.analytics.screenTracking

import android.view.Window
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import junit.framework.TestCase.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
        // Add many activities to increase iteration time
        val activityLifecycleTracker = getActivityLifecycleTracker()
        val activities = List(500) { mockActivity() }

        activities.forEach {
            activityLifecycleTracker.onActivityCreated(it, null)
        }

        val destroyThread = Thread {
            repeat(100) {
                activityLifecycleTracker.onActivityDestroyed(activities.random())
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

    @Test
    fun `when onActivityCreated called while unregister is executing should not throw ConcurrentModificationException`() {
        val activityLifecycleTracker = getActivityLifecycleTracker()
        // Add many activities to increase iteration time
        val activities = ArrayList(List(500) { mockActivity() })
        activities.forEach {
            activityLifecycleTracker.onActivityCreated(it, null)
        }

        val destroyThread = Thread {
            repeat(100) {
                activityLifecycleTracker.onActivityCreated(mockActivity(), null)
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