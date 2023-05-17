package com.bluetriangle.analytics.screenTracking

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

@RequiresApi(Build.VERSION_CODES.O)
class FragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    private var visibleFragment: Fragment? = null

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        if (visibleFragment == null || visibleFragment!!.javaClass.simpleName != fragment.javaClass.simpleName) {
            visibleFragment = fragment
            Log.i("Frg Screen Tracking", visibleFragment!!.javaClass.simpleName)
            //TODO:: report screen view event
        }
    }
}