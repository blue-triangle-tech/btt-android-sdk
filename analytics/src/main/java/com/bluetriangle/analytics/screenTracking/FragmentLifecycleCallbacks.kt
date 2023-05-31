package com.bluetriangle.analytics.screenTracking

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bluetriangle.analytics.Timer
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

inline fun <reified T : Any, R> T.getPrivateProperty(name: String): R? =
    T::class
        .memberProperties
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.get(this) as R?

fun Fragment.getUniqueId(): String? {
    return this.getPrivateProperty<Fragment, String>("mWho")
}

@RequiresApi(Build.VERSION_CODES.O)
internal class FragmentLifecycleCallbacks(private val callback: IScreenTrackCallback) :
    FragmentManager.FragmentLifecycleCallbacks() {
    private val fragmentLoadMap = mutableMapOf<String, Timer>()
    private val fragmentViewMap = mutableMapOf<String, Timer>()

    override fun onFragmentPreAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logToLogcat("Fragment Pre Attached", fragment.javaClass.simpleName)
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        logToLogcat("Fragment Attached", fragment.javaClass.simpleName)
    }

    override fun onFragmentPreCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        logToLogcat("Fragment Pre Created", fragment.javaClass.simpleName)
    }

    override fun onFragmentCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        savedInstanceState: Bundle?
    ) {
        val className = fragment.javaClass.simpleName
        logToLogcat("Fragment Created", className)

        val fragmentId = fragment.getUniqueId()
        if (fragmentId != null) {
            fragmentLoadMap[fragmentId] =
                Timer("$className - loaded", "AutomaticScreenTrack").start()
        }
    }

    override fun onFragmentViewCreated(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        view: View,
        savedInstanceState: Bundle?
    ) {
        logToLogcat("Fragment View Created", fragment.javaClass.simpleName)
    }

    override fun onFragmentStarted(fragmentManager: FragmentManager, fragment: Fragment) {
        logToLogcat("Fragment Started", fragment.javaClass.simpleName)
    }

    override fun onFragmentResumed(fragmentManager: FragmentManager, fragment: Fragment) {
        val className = fragment.javaClass.simpleName
        logToLogcat("Fragment Resumed", className)

        val fragmentId = fragment.getUniqueId()
        if (fragmentId != null) {
            val createTime = fragmentLoadMap.remove(fragmentId)
            if (createTime != null) {
                callback.onScreenLoad(
                    className,
                    className,
                    createTime
                )
            }

            fragmentViewMap[fragmentId] =
                Timer("$className - viewed", "AutomaticScreenTrack").start()
        }
    }

    override fun onFragmentPaused(fragmentManager: FragmentManager, fragment: Fragment) {
        val className = fragment.javaClass.simpleName
        logToLogcat("Fragment Paused", className)

        val fragmentId = fragment.getUniqueId()
        if (fragmentId != null) {
            val resumeTime = fragmentViewMap.remove(fragmentId)
            if (resumeTime != null) {
                callback.onScreenView(
                    className,
                    className,
                    resumeTime
                )
            }
        }
    }

    override fun onFragmentStopped(fragmentManager: FragmentManager, fragment: Fragment) {
        logToLogcat("Fragment Stopped", fragment.javaClass.simpleName)
    }

    override fun onFragmentSaveInstanceState(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        outState: Bundle
    ) {
    }

    override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logToLogcat("Fragment View Destroyed", fragment.javaClass.simpleName)
    }

    override fun onFragmentDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
        logToLogcat("Fragment Destroyed", fragment.javaClass.simpleName)
    }

    override fun onFragmentDetached(fragmentManager: FragmentManager, fragment: Fragment) {
        logToLogcat("Fragment Detached", fragment.javaClass.simpleName)
    }

    private fun logToLogcat(tag: String, msg: String) {
        //Log.i(tag, msg)
    }
}