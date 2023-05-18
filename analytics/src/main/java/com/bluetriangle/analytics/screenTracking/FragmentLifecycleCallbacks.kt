package com.bluetriangle.analytics.screenTracking

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


@RequiresApi(Build.VERSION_CODES.O)
class FragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    private var visibleFragment: Fragment? = null

    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        Log.i("Fragment Pre Attached", fragment.javaClass.simpleName)
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        Log.i("Fragment Attached", fragment.javaClass.simpleName)
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        Log.i("Fragment Pre Created", fragment.javaClass.simpleName)
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        Log.i("Fragment Created", fragment.javaClass.simpleName)
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
        Log.i("Fragment View Created", fragment.javaClass.simpleName)
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment Started", fragment.javaClass.simpleName)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        if (visibleFragment == null || visibleFragment!!.javaClass.simpleName != fragment.javaClass.simpleName) {
            visibleFragment = fragment
            Log.i("Fragment Resumed", visibleFragment!!.javaClass.simpleName)
        }
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment Paused", fragment.javaClass.simpleName)
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment Stopped", fragment.javaClass.simpleName)
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment View Destroyed", fragment.javaClass.simpleName)
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment Destroyed", fragment.javaClass.simpleName)
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        Log.i("Fragment Detached", fragment.javaClass.simpleName)
    }
}