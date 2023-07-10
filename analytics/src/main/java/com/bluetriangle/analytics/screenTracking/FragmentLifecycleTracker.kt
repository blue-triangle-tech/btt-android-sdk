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
        logD(TAG, "onFragmentPreAttached")
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logD(TAG, "onFragmentAttached")
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentPreCreated")
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentCreated")
        screenTrackMonitor.onLoadStarted(fragment.screen)
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
        logD(TAG, "onFragmentViewCreated")
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentStarted")
        screenTrackMonitor.onLoadEnded(fragment.screen)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentResumed")
        screenTrackMonitor.onViewStarted(fragment.screen)
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentPaused")
        screenTrackMonitor.onViewEnded(fragment.screen)
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentStopped")
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
        logD(TAG, "onFragmentSaveInstanceState")
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentViewDestroyed")
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentDestroyed")
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        logD(TAG, "onFragmentDetached")
    }
}