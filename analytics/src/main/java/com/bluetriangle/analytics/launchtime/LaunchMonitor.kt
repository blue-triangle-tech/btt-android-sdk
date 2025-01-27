package com.bluetriangle.analytics.launchtime

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.os.SystemClock
import android.util.Log
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.appeventhub.AppEventConsumer
import com.bluetriangle.analytics.launchtime.model.AppLifecycleEvent
import com.bluetriangle.analytics.launchtime.model.LaunchEvent
import com.bluetriangle.analytics.launchtime.helpers.AppEventAccumulator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
internal class LaunchMonitor private constructor(): AppEventConsumer, LaunchEventProducer, LogHolder {

    companion object {
        private const val LOG_BUFFER_SIZE = 30
        private var _instance:LaunchMonitor?=null

        val instance:LaunchMonitor
            get() {
                if(_instance == null) {
                    _instance = LaunchMonitor()
                }
                return _instance!!
            }
    }

    private val appEventAccumulator = AppEventAccumulator(this)

    private val _logs = mutableListOf<LogData>()
    override val logs: List<LogData>
        get() = _logs

    override fun log(logData: LogData) {
        if(Tracker.instance != null) {
            Tracker.instance?.configuration?.logger?.log(logData.level, logData.message)
            return
        }
        while(_logs.size >= LOG_BUFFER_SIZE) {
            _logs.removeAt(0)
        }
        _logs.add(logData)
    }

    init {
        log(LogData(message = "${getPrefix()} initialized"))
    }

    private fun getPrefix() = "${getAppUpTime()} : LaunchMonitor :"

    private fun getAppUpTime() = SystemClock.uptimeMillis() - (if(Build.VERSION.SDK_INT >= 24) Process.getStartUptimeMillis() else 0)

    override fun onAppCreated(application:Application) {
        log(LogData(level = Log.VERBOSE, message = "${getPrefix()} onAppCreated"))
        appEventAccumulator.accumulate(AppLifecycleEvent.AppLifecycleCreated())
    }

    override fun onActivityCreated(activity: Activity, data: Bundle?) {
        log(LogData(level = Log.VERBOSE, message = "${getPrefix()} onActivityCreated: $data"))
        appEventAccumulator.accumulate(AppLifecycleEvent.ActivityCreated())
    }

    override fun onActivityStarted(activity: Activity) {
        log(LogData(level = Log.VERBOSE, message = "${getPrefix()} onActivityStarted"))
        appEventAccumulator.accumulate(AppLifecycleEvent.ActivityStarted())
    }

    override fun onActivityResumed(activity: Activity) {
        log(LogData(level = Log.VERBOSE, message = "${getPrefix()} onActivityResumed"))
        val result = appEventAccumulator.accumulate(AppLifecycleEvent.ActivityResumed())
        if(result != null) {
            val activityName = activity::class.java.simpleName
            val startTime = result.events[0].time
            log(LogData(level = Log.VERBOSE, message = "${getPrefix()} LaunchEvent ${result.type.name} took ${System.currentTimeMillis() - startTime}ms"))

            GlobalScope.launch {
                // Remove if there is a launch event is in the channel that hasn't been processed yet
                // so as to keep only one LaunchEvent in the channel
                _launchEvents.tryReceive()
                _launchEvents.send(LaunchEvent.create(result.type, activityName, startTime))
            }
        }
    }

    override fun onAppMovedToBackground(application:Application) {
        log(LogData(level = Log.VERBOSE, message = "${getPrefix()} onAppMovedToBackground"))
    }

    private val _launchEvents = Channel<LaunchEvent>(1)
    override val launchEvents: ReceiveChannel<LaunchEvent>
        get() = _launchEvents

}