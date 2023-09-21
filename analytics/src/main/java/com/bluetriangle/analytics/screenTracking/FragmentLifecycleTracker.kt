package com.bluetriangle.analytics.screenTracking

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.utility.logD
import com.bluetriangle.analytics.utility.screen

internal class FragmentLifecycleTracker(val screenTrackMonitor: ScreenLifecycleTracker) : FragmentManager.FragmentLifecycleCallbacks() {

    private val TAG = this::class.java.simpleName

    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logD(TAG, "onFragmentPreAttached: ${fragment.screen}")
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logD(TAG, "onFragmentAttached: ${fragment.screen}")
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentPreCreated: ${fragment.screen}")
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentCreated: ${fragment.screen}")
        screenTrackMonitor.onLoadStarted(fragment.screen)
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentViewCreated: ${fragment.screen}")
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentStarted: ${fragment.screen}")
        screenTrackMonitor.onLoadEnded(fragment.screen)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentResumed: ${fragment.screen}")
        screenTrackMonitor.onViewStarted(fragment.screen)
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentPaused: ${fragment.screen}")
        screenTrackMonitor.onViewEnded(fragment.screen)
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentStopped: ${fragment.screen}")
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
        logD(TAG, "onFragmentSaveInstanceState: ${fragment.screen}")
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentViewDestroyed: ${fragment.screen}")
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentDestroyed: ${fragment.screen}")
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentDetached: ${fragment.screen}")
    }
}