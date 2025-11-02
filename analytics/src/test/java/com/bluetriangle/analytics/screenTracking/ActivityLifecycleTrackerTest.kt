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

    @Test
    fun `when onActivityDestroyed called while unregister is executing should not throw ConcurrentModificationException`() {
        val screenTracker = mock(ScreenLifecycleTracker::class.java)
        val fragmentTracker = mock(FragmentLifecycleTracker::class.java)
        val tracker = ActivityLifecycleTracker(screenTracker, fragmentTracker)

        val mockWindow = mock(Window::class.java)
        val callback = mock(Window.Callback::class.java)
        val fragmentManager = mock(FragmentManager::class.java)

        `when`(mockWindow.callback).thenReturn(callback)

        // Add many activities to increase iteration time
        val activities = List(500) { mock(FragmentActivity::class.java) }
        activities.forEach {
            `when`(it.window).thenReturn(mockWindow)
            `when`(it.supportFragmentManager).thenReturn(fragmentManager)
            tracker.onActivityCreated(it, null)
        }

        val destroyThread = Thread {
            repeat(100) {
                tracker.onActivityDestroyed(activities.random())
            }
        }

        destroyThread.start()
        // While other thread removes, we iterate
        try {
            tracker.unregister()
        } catch (e: ConcurrentModificationException) {
            fail("Should not throw ConcurrentModificationException")
        }

        destroyThread.join()
    }

    @Test
    fun `when onActivityCreated called while unregister is executing should not throw ConcurrentModificationException`() {
        val screenTracker = mock(ScreenLifecycleTracker::class.java)
        val fragmentTracker = mock(FragmentLifecycleTracker::class.java)
        val tracker = ActivityLifecycleTracker(screenTracker, fragmentTracker)

        val mockWindow = mock(Window::class.java)
        val callback = mock(Window.Callback::class.java)
        val fragmentManager = mock(FragmentManager::class.java)

        `when`(mockWindow.callback).thenReturn(callback)

        // Add many activities to increase iteration time
        val activities = ArrayList(List(500) { mock(FragmentActivity::class.java) })
        activities.forEach {
            `when`(it.window).thenReturn(mockWindow)
            `when`(it.supportFragmentManager).thenReturn(fragmentManager)
            tracker.onActivityCreated(it, null)
        }

        val destroyThread = Thread {
            repeat(100) {
                val mockActivity = mock(FragmentActivity::class.java)
                `when`(mockActivity.window).thenReturn(mockWindow)
                `when`(mockActivity.supportFragmentManager).thenReturn(fragmentManager)
                tracker.onActivityCreated(mockActivity, null)
            }
        }

        destroyThread.start()
        // While other thread removes, we iterate
        try {
            tracker.unregister()
        } catch (e: ConcurrentModificationException) {
            fail("Should not throw ConcurrentModificationException")
        }

        destroyThread.join()
    }
}