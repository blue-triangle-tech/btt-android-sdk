package com.bluetriangle.analytics.lifecycle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal class FragmentLifecycleDispatcher : FragmentManager.FragmentLifecycleCallbacks() {

    private val observers = mutableSetOf<FragmentLifecycleObserver>()

    fun addObserver(observer: FragmentLifecycleObserver) {
        observers += observer
    }

    fun removeObserver(observer: FragmentLifecycleObserver) {
        observers -= observer
    }

    override fun onFragmentPreAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
        observers.forEach { it.onPreAttached(fragment, context) }
    }

    override fun onFragmentAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
        observers.forEach { it.onAttached(fragment, context) }
    }

    override fun onFragmentPreCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        observers.forEach { it.onPreCreated(fragment, savedInstanceState) }
    }

    override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        observers.forEach { it.onCreated(fragment, savedInstanceState) }
    }

    override fun onFragmentViewCreated(fm: FragmentManager, fragment: Fragment, v: View, savedInstanceState: Bundle?) {
        observers.forEach { it.onViewCreated(fragment, v, savedInstanceState) }
    }

    override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onStarted(fragment) }
    }

    override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onResumed(fragment) }
    }

    override fun onFragmentPaused(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onPaused(fragment) }
    }

    override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onStopped(fragment) }
    }

    override fun onFragmentSaveInstanceState(fm: FragmentManager, fragment: Fragment, outState: Bundle) {
        observers.forEach { it.onSaveInstanceState(fragment, outState) }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onViewDestroyed(fragment) }
    }

    override fun onFragmentDestroyed(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onDestroyed(fragment) }
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        observers.forEach { it.onDetached(fragment) }
    }

    fun clear() {
        observers.clear()
    }

}