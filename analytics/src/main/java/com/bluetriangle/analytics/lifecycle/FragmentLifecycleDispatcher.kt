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

    fun onFragmentPreAttached(fragment: Fragment, context: Context) {
        observers.forEach { it.onPreAttached(fragment, context) }
    }

    fun onFragmentAttached(fragment: Fragment, context: Context) {
        observers.forEach { it.onAttached(fragment, context) }
    }

    fun onFragmentPreCreated(fragment: Fragment, savedInstanceState: Bundle?) {
        observers.forEach { it.onPreCreated(fragment, savedInstanceState) }
    }

    fun onFragmentCreated(fragment: Fragment, savedInstanceState: Bundle?) {
        observers.forEach { it.onCreated(fragment, savedInstanceState) }
    }

    fun onFragmentActivityCreated(fragment: Fragment, savedInstanceState: Bundle?) {
        observers.forEach { it.onActivityCreated(fragment, savedInstanceState) }
    }

    fun onFragmentViewCreated(fragment: Fragment, v: View, savedInstanceState: Bundle?) {
        observers.forEach { it.onViewCreated(fragment, v, savedInstanceState) }
    }

    fun onFragmentStarted(fragment: Fragment) {
        observers.forEach { it.onStarted(fragment) }
    }

    fun onFragmentResumed(fragment: Fragment) {
        observers.forEach { it.onResumed(fragment) }
    }

    fun onFragmentPaused(fragment: Fragment) {
        observers.forEach { it.onPaused(fragment) }
    }

    fun onFragmentStopped(fragment: Fragment) {
        observers.forEach { it.onStopped(fragment) }
    }

    fun onFragmentSaveInstanceState(fragment: Fragment, outState: Bundle) {
        observers.forEach { it.onSaveInstanceState(fragment, outState) }
    }

    fun onFragmentViewDestroyed(fragment: Fragment) {
        observers.forEach { it.onViewDestroyed(fragment) }
    }

    fun onFragmentDestroyed(fragment: Fragment) {
        observers.forEach { it.onDestroyed(fragment) }
    }

    fun onFragmentDetached(fragment: Fragment) {
        observers.forEach { it.onDetached(fragment) }
    }

    fun clear() {
        observers.clear()
    }

}