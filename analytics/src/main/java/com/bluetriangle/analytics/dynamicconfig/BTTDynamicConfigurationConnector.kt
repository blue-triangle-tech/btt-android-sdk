package com.bluetriangle.analytics.dynamicconfig

import android.app.Activity
import android.app.Application
import android.util.Log
import com.bluetriangle.analytics.AppEventHub
import com.bluetriangle.analytics.dynamicconfig.updater.BTTConfigurationUpdater
import com.bluetriangle.analytics.dynamicconfig.updater.IBTTConfigurationUpdater
import com.bluetriangle.analytics.launchtime.AppEventConsumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class BTTDynamicConfigurationConnector(
    private val updater: IBTTConfigurationUpdater
) : AppEventConsumer {

    private var scope: CoroutineScope? = null

    override fun onActivityResumed(activity: Activity) {
        super.onActivityResumed(activity)
        initScope()

        scope?.launch {
            updater.update()
        }
    }

    private fun initScope() {
        scope = CoroutineScope(Dispatchers.IO)
    }

    override fun onAppMovedToBackground(application: Application) {
        super.onAppMovedToBackground(application)
        destroyScope()
    }

    private fun destroyScope() {
        scope?.cancel()
        scope = null
    }
}