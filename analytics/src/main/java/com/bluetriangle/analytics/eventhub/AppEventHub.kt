package com.bluetriangle.analytics.eventhub

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ProcessLifecycleOwner
import com.bluetriangle.analytics.eventhub.helpers.ActivityEventHandler
import com.bluetriangle.analytics.eventhub.helpers.AppBackgroundNotifier
import java.lang.ref.WeakReference

internal class AppEventHub private constructor(): AppEventConsumer {

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
                consumers.add(WeakReference(consumer))
            }
        }
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

    override fun onAppCreated(application: Application) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppBackgroundNotifier(application))
        application.registerActivityLifecycleCallbacks(activityEventHandler)

        notifyConsumers {
            it.onAppCreated(application)
        }
    }

    override fun onActivityCreated(activity: Activity, data: Bundle?) {
        notifyConsumers {
            it.onActivityCreated(activity, data)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        notifyConsumers {
            it.onActivityStarted(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        activity.application.unregisterActivityLifecycleCallbacks(activityEventHandler)

        notifyConsumers {
            it.onActivityResumed(activity)
        }
    }

    override fun onAppMovedToBackground(application: Application) {
        application.registerActivityLifecycleCallbacks(activityEventHandler)

        notifyConsumers {
            it.onAppMovedToBackground(application)
        }
    }


}