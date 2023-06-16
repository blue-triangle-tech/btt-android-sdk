package com.bluetriangle.analytics.utility

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.model.ViewType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun logD(tag:String, message:String) {
    Tracker.instance?.configuration?.logger?.debug("$tag: $message")
}

inline fun <reified T : Any, reified R> T.getPrivateProperty(name: String): R? {
    val property = T::class
        .memberProperties
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.get(this)
    if(property is R) {
        return property
    }
    return null
}

fun Fragment.getUniqueId(): String? {
    return this.getPrivateProperty<Fragment, String>("mWho")
}

internal val Fragment.screen: Screen
    get() = Screen(
        getUniqueId()?:"",
        this::class.java.simpleName,
        ViewType.Fragment
    )

internal val Activity.screen: Screen
    get() = Screen(
        hashCode().toString(),
        this::class.java.simpleName,
        ViewType.Activity
    )

fun FragmentActivity.registerFragmentLifecycleCallback(callback:FragmentLifecycleCallbacks) {
    supportFragmentManager.registerFragmentLifecycleCallbacks(callback,true)
}

fun FragmentActivity.unregisterFragmentLifecycleCallback(callback:FragmentLifecycleCallbacks) {
    supportFragmentManager.unregisterFragmentLifecycleCallbacks(callback)
}