package com.bluetriangle.analytics.screenTracking

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.utility.screen

internal class FragmentLifecycleTracker(val screenTrackMonitor: ScreenLifecycleTracker) : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        screenTrackMonitor.onLoadStarted(fragment.screen)
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        screenTrackMonitor.onLoadEnded(fragment.screen)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        screenTrackMonitor.onViewStarted(fragment.screen)
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        screenTrackMonitor.onViewEnded(fragment.screen)
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
    }
}