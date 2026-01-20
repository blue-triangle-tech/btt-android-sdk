package com.bluetriangle.analytics.eventhub.helpers

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bluetriangle.analytics.eventhub.AppEventHub

internal class AppBackgroundNotifier(val application: Application):DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        AppEventHub.instance.onAppMovedToBackground(application)
    }

}
