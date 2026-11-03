package com.bluetriangle.analytics.applaunch

import android.app.ApplicationExitInfo.REASON_ANR
import android.app.ApplicationExitInfo.REASON_CRASH
import android.app.ApplicationExitInfo.REASON_USER_REQUESTED
import com.bluetriangle.analytics.applaunch.ForceRestartTracker.Companion.isForceRestart
import com.bluetriangle.analytics.applaunch.ForceRestartTracker.Companion.leftAppAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForceRestartTrackerTest {

    private val duration = 10_000L
    private val now = 1_000_000_000L
    private val inForeground = 0L

    @Test
    fun `killed while in foreground and relaunched within duration is a force restart`() {
        // e.g. swiped away from a Recents live tile, or force stopped while on screen
        assertTrue(isForceRestart(now, exitTime = now - 2_000, REASON_USER_REQUESTED, inForeground, duration))
    }

    @Test
    fun `time on the screen before the kill doesn't count`() {
        // Product Details visible for 14 s, then killed and relaunched 2 s later
        assertTrue(isForceRestart(now, exitTime = now - 2_000, REASON_USER_REQUESTED, inForeground, duration))
    }

    @Test
    fun `backgrounded, swiped away and relaunched within duration is a force restart`() {
        val background = now - 5_000
        assertTrue(isForceRestart(now, exitTime = now - 2_000, REASON_USER_REQUESTED, background, duration))
    }

    @Test
    fun `backgrounded 30 min before being swiped away is not a force restart`() {
        val background = now - 30 * 60_000
        assertFalse(isForceRestart(now, exitTime = now - 2_000, REASON_USER_REQUESTED, background, duration))
    }

    @Test
    fun `window starts when the app went to the background, not at the kill`() {
        // left in Recents for 8 s before swiping away, relaunched 3 s later: 11 s since leaving the app
        val background = now - 11_000
        assertFalse(isForceRestart(now, exitTime = now - 3_000, REASON_USER_REQUESTED, background, duration))
    }

    @Test
    fun `relaunch after duration is not a force restart`() {
        assertFalse(isForceRestart(now, exitTime = now - 10_637, REASON_USER_REQUESTED, inForeground, duration))
    }

    @Test
    fun `ANR close and crash are not force restarts`() {
        assertFalse(isForceRestart(now, exitTime = now - 2_000, REASON_ANR, inForeground, duration))
        assertFalse(isForceRestart(now, exitTime = now - 2_000, REASON_CRASH, inForeground, duration))
    }

    @Test
    fun `left the app at background time only when it went to background before the kill`() {
        assertEquals(500L, leftAppAt(exitTime = 900, backgroundTime = 500))
        assertEquals(900L, leftAppAt(exitTime = 900, backgroundTime = 0))
        assertEquals(900L, leftAppAt(exitTime = 900, backgroundTime = 950))
    }
}
