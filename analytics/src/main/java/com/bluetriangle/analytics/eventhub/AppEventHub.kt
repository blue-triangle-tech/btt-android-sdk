package com.bluetriangle.analytics.eventhub

import android.app.Activity
import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import com.bluetriangle.analytics.eventhub.helpers.ActivityEventHandler
import com.bluetriangle.analytics.eventhub.helpers.AppBackgroundNotifier
import java.lang.ref.WeakReference

internal class AppEventHub private constructor(): ComponentCallbacks2 {

    companion object {
        private var _instance: AppEventHub? = null

        val instance: AppEventHub
            get() {
                if (_instance == null) {
                    _instance = AppEventHub()
                }
                return _instance!!
            }
    }

    private val consumers = arrayListOf<WeakReference<AppEventConsumer>>()

    fun addConsumer(consumer: AppEventConsumer) {
        synchronized(consumers) {
            if(consumers.find { it.get() == consumer } == null) {
                notifyAppCreated(consumer)
                consumers.add(WeakReference(consumer))
            }
        }
    }

    fun notifyAppCreated(consumer: AppEventConsumer) {
        val app = application?.get() ?: return
        val timestamp = appCreatedTimestamp?: return

        consumer.onAppCreated(app, timestamp)
    }

    fun removeConsumer(consumer: AppEventConsumer) {
        synchronized(consumers) {
            consumers.removeAll { reference -> reference.get() == consumer }
        }
    }

    private val activityEventHandler = ActivityEventHandler()

    private fun notifyConsumers(notify:(AppEventConsumer)-> Unit) {
        synchronized(consumers) {
            consumers.forEach { consumer ->
                consumer.get()?.let {
                    notify(it)
                }
            }
        }
    }

    private var appCreatedTimestamp: Long? = null
    private var application: WeakReference<Application>? = null

    fun onAppCreated(application: Application, timestamp: Long) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppBackgroundNotifier(application))
        application.registerComponentCallbacks(this)
        application.registerActivityLifecycleCallbacks(activityEventHandler)
        appCreatedTimestamp = timestamp
        this.application = WeakReference(application)
        notifyConsumers {
            it.onAppCreated(application, timestamp)
        }
    }

    fun onActivityCreated(activity: Activity, data: Bundle?) {
        notifyConsumers {
            it.onActivityCreated(activity, data)
        }
    }

    fun onActivityStarted(activity: Activity) {
        notifyConsumers {
            it.onActivityStarted(activity)
        }
    }

    fun onActivityResumed(activity: Activity) {
        activity.application.unregisterActivityLifecycleCallbacks(activityEventHandler)

        notifyConsumers {
            it.onActivityResumed(activity)
        }
    }

    fun onAppMovedToBackground(application: Application) {
        application.registerActivityLifecycleCallbacks(activityEventHandler)

        notifyConsumers {
            it.onAppMovedToBackground(application)
        }
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        notifyConsumers {
            it.onConfigurationChanged(configuration)
        }
    }

    override fun onLowMemory() {
        notifyConsumers {
            it.onLowMemory()
        }
    }

    override fun onTrimMemory(level: Int) {
        notifyConsumers { consumer ->
            val levelString = when(level) {
                ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "TRIM_MEMORY_COMPLETE"
                ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "TRIM_MEMORY_COMPLETE"
                ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "TRIM_MEMORY_MODERATE"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "TRIM_MEMORY_RUNNING_CRITICAL"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "TRIM_MEMORY_RUNNING_LOW"
                ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "TRIM_MEMORY_RUNNING_MODERATE"
                else -> null
            }
            levelString?.let {
                consumer.onTrimMemory(it)
            }
        }
    }

}