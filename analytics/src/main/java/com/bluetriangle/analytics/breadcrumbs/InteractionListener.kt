package com.bluetriangle.analytics.breadcrumbs

import android.app.Activity
import android.app.Application
import android.os.Bundle

class InteractionListener : Application.ActivityLifecycleCallbacks {

    fun install(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun uninstall(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        bundle: Bundle?,
    ) {
        activity.window.callback = InteractionWindowCallback(activity, activity.window.callback)
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityResumed(p0: Activity) {}

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(
        p0: Activity,
        p1: Bundle,
    ) {}

    override fun onActivityDestroyed(p0: Activity) {}
}