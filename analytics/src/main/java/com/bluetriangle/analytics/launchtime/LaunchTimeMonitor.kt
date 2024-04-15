package com.bluetriangle.analytics.launchtime

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.model.NativeAppProperties
import com.bluetriangle.analytics.utility.logD


class LaunchTimeMonitor private constructor(application: Application) : ActivityLifecycleCallbacks,
    LifecycleEventObserver {

    private var initTime = -1L
    private var isAppInBackground = false
    private var wasAppInBackground = false
    private var launchType:LaunchType?=null

    enum class LaunchType {
        Hot,
        Cold,
        Warm
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    companion object {
        private var instance: LaunchTimeMonitor? = null

        val TAG = LaunchTimeMonitor::class.java.simpleName

        fun initialize(application: Application) {
            if(instance != null) return

            instance = LaunchTimeMonitor(application)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if(initTime == -1L && wasAppInBackground) {
            wasAppInBackground = false
            launchType = LaunchType.Warm
            initTime = System.currentTimeMillis()
        }
        logD(TAG, "${activity::class.java.simpleName}::onActivityCreated")
    }

    override fun onActivityStarted(activity: Activity) {
        logD(TAG, "${activity::class.java.simpleName}::onActivityStarted::$wasAppInBackground::$isAppInBackground")
        if(initTime == -1L && (wasAppInBackground || isAppInBackground)) {
            wasAppInBackground = false
            isAppInBackground = false
            launchType = LaunchType.Hot
            initTime = System.currentTimeMillis()
        }
    }

    override fun onActivityResumed(activity: Activity) {
        logD(TAG, "${activity::class.java.simpleName}::onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity) {
        logD(TAG, "${activity::class.java.simpleName}::onActivityPaused")
    }

    override fun onActivityPostResumed(activity: Activity) {
        super.onActivityPostResumed(activity)
        if(initTime == -1L) return
        wasAppInBackground = false
        val launchTime = System.currentTimeMillis() - initTime
        initTime = -1L
        val timer = Timer()
        timer.startWithoutPerformanceMonitor()
        timer.setPageName("LaunchTime")
        timer.pageTimeCalculator = {
            launchTime
        }
        timer.submit()
        logD(TAG, "launchTime: $launchTime")
    }

    override fun onActivityStopped(activity: Activity) {
        logD(TAG, "${activity::class.java.simpleName}::onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logD(TAG, "${activity::class.java.simpleName}::onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logD(TAG, "${activity::class.java.simpleName}::onActivityDestroyed")
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if(event == Lifecycle.Event.ON_CREATE) {
            onAppCreated()
        } else if(event == Lifecycle.Event.ON_START) {
            onAppForeground()
        } else if(event == Lifecycle.Event.ON_STOP) {
            onAppBackground()
        }
    }

    private fun onAppCreated() {
        launchType = LaunchType.Cold
        initTime = System.currentTimeMillis()
        Log.d("BlueTriangle", "$TAG: --------APP CREATED--------")
    }

    private fun onAppBackground() {
        isAppInBackground = true
        initTime = -1L
        logD(TAG, "--------MOVED TO BACKGROUND--------")
    }

    private fun onAppForeground() {
        if(isAppInBackground) {
            wasAppInBackground = true
        }
        isAppInBackground = false
        logD(TAG, "--------MOVED TO FOREGROUND--------")
    }
}