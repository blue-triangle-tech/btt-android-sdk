package com.bluetriangle.analytics.lifecycle

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

internal interface FragmentLifecycleObserver {
    fun onPreAttached(fragment: Fragment, context: Context) {}
    fun onAttached(fragment: Fragment, context: Context) {}
    fun onPreCreated(fragment: Fragment, savedInstanceState: Bundle?) {}
    fun onCreated(fragment: Fragment, savedInstanceState: Bundle?) {}
    fun onViewCreated(fragment: Fragment, v: View, savedInstanceState: Bundle?) {}
    fun onStarted(fragment: Fragment) {}
    fun onResumed(fragment: Fragment) {}
    fun onPaused(fragment: Fragment) {}
    fun onStopped(fragment: Fragment) {}
    fun onSaveInstanceState(fragment: Fragment, outState: Bundle) {}
    fun onViewDestroyed(fragment: Fragment) {}
    fun onDestroyed(fragment: Fragment) {}
    fun onDetached(fragment: Fragment) {}
}