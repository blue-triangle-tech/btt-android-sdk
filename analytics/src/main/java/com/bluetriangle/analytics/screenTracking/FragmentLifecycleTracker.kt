package com.bluetriangle.analytics.screenTracking

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.utility.getToolbarTitle
import com.bluetriangle.analytics.utility.screen

internal class FragmentLifecycleTracker(val screenTrackMonitor: ScreenLifecycleTracker) : FragmentManager.FragmentLifecycleCallbacks() {

    companion object {
        const val TAG = "FragmentLifecycleTracker"
    }

    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logEvent("onFragmentPreAttached", fragment)
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logEvent("onFragmentAttached", fragment)
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logEvent("onFragmentPreCreated", fragment)
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logEvent("onFragmentCreated", fragment)
        screenTrackMonitor.onLoadStarted(fragment.screen)
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
        logEvent("onFragmentViewCreated", fragment)
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentStarted", fragment)
        screenTrackMonitor.onLoadEnded(fragment.screen)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        val screen = fragment.screen
        logEvent("onFragmentResumed", fragment)
        Handler(Looper.getMainLooper()).postDelayed({
            fragment.activity?.getToolbarTitle()?.let {
                screen.title = it
            }
        }, 400)
        screenTrackMonitor.onViewStarted(screen)
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentPaused", fragment)
        screenTrackMonitor.onViewEnded(fragment.screen)
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentStopped", fragment)
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
        logEvent("onFragmentSaveInstanceState", fragment)
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentViewDestroyed", fragment)
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentDestroyed", fragment)
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        logEvent("onFragmentDetached", fragment)
    }

    fun logEvent(event: String, fragment: Fragment) {
        Tracker.instance?.configuration?.logger?.debug("$TAG, $event: ${fragment::class.java.simpleName}")
    }
}