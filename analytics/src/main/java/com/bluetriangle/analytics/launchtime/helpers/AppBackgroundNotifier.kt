package com.bluetriangle.analytics.launchtime.helpers

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluetriangle.analytics.launchtime.AppEventConsumer

internal class AppBackgroundNotifier(val application: Application, val listener: AppEventConsumer):DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        listener.onAppMovedToBackground(application)
    }

}
