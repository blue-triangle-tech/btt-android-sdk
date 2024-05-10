package com.bluetriangle.analytics.launchtime

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.launchtime.data.AppEvent
import com.bluetriangle.analytics.launchtime.data.LaunchEvent
import com.bluetriangle.analytics.launchtime.helpers.ActivityEventHandler
import com.bluetriangle.analytics.launchtime.helpers.AppBackgroundNotifier
import com.bluetriangle.analytics.launchtime.helpers.AppEventAccumulator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
internal class LaunchMonitor private constructor():AppEventConsumer, LaunchEventProducer {

    private val logger = Tracker.instance?.configuration?.logger

    companion object {
        private var _instance:LaunchMonitor?=null

        val instance:LaunchMonitor
            get() {
                if(_instance == null) {
                    _instance = LaunchMonitor()
                }
                return _instance!!
            }
    }

    private val activityEventHandler = ActivityEventHandler(this)
    private val appEventAccumulator = AppEventAccumulator()

    override fun onAppCreated(application:Application) {
        application.registerActivityLifecycleCallbacks(activityEventHandler)
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppBackgroundNotifier(application, this))
        appEventAccumulator.accumulate(AppEvent.AppCreated())
    }

    override fun onActivityStarted(activity: Activity) {
        appEventAccumulator.accumulate(AppEvent.ActivityStarted())
    }

    override fun onActivityResumed(activity: Activity) {
        val result = appEventAccumulator.accumulate(AppEvent.ActivityResumed())
        if(result != null) {
            val activityName = activity::class.java.simpleName
            val startTime = result.events[0].time
            GlobalScope.launch {
                logger?.debug("Launch event detected: ${result.type}")
                _launchEvents.send(LaunchEvent.create(result.type, activityName, startTime))
            }
        }
        activity.application.unregisterActivityLifecycleCallbacks(activityEventHandler)
    }

    override fun onAppMovedToBackground(application:Application) {
        application.registerActivityLifecycleCallbacks(activityEventHandler)
    }

    private val _launchEvents = Channel<LaunchEvent>(UNLIMITED)
    override val launchEvents: ReceiveChannel<LaunchEvent>
        get() = _launchEvents

}