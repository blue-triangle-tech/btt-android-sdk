package com.bluetriangle.analytics.launchtime.helpers

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluetriangle.analytics.AppEventHub

internal class AppBackgroundNotifier(val application: Application):DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        AppEventHub.instance.onAppMovedToBackground(application)
    }

}
