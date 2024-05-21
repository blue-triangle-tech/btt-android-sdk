package com.bluetriangle.analytics.utility

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import android.system.Os
import android.system.OsConstants
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.model.Screen
import com.bluetriangle.analytics.model.ScreenType
import java.io.File
import com.bluetriangle.analytics.networkstate.BTTNetworkState
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun logD(tag: String, message: String) {
    Tracker.instance?.configuration?.logger?.debug("$tag: $message")
}

inline fun <reified T : Any, reified R> T.getPrivateProperty(name: String): R? {
    val property = T::class
        .memberProperties
        .firstOrNull { it.name == name }
        ?.apply { isAccessible = true }
        ?.get(this)
    if (property is R) {
        return property
    }
    return null
}

fun Fragment.getUniqueId(): String? {
    return this.getPrivateProperty<Fragment, String>("mWho")
}

internal val Fragment.screen: Screen
    get() = Screen(
        getUniqueId() ?: "",
        this::class.java.simpleName,
        ScreenType.Fragment
    )

internal val Activity.screen: Screen
    get() = Screen(
        hashCode().toString(),
        this::class.java.simpleName,
        ScreenType.Activity
    )

fun FragmentActivity.registerFragmentLifecycleCallback(callback: FragmentLifecycleCallbacks) {
    supportFragmentManager.registerFragmentLifecycleCallbacks(callback, true)
}

fun FragmentActivity.unregisterFragmentLifecycleCallback(callback: FragmentLifecycleCallbacks) {
    supportFragmentManager.unregisterFragmentLifecycleCallbacks(callback)
}

val Long.mb:Long
    get() = this/1024 * 1024

fun <V> any(vararg operations:()->V?):V? {
    operations.forEach {
        val value = it()
        if(value != null) {
            return@any value
        }
    }
    return null
}

val File.isDirectoryInvalid: Boolean
    get() = !isDirectory || !canRead() || !canWrite()

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val wifiTransports = intArrayOf(
    NetworkCapabilities.TRANSPORT_WIFI,
    NetworkCapabilities.TRANSPORT_BLUETOOTH,
    NetworkCapabilities.TRANSPORT_VPN
)

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val cellularTransports = intArrayOf(NetworkCapabilities.TRANSPORT_CELLULAR)

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val ethernetTransports = intArrayOf(NetworkCapabilities.TRANSPORT_ETHERNET)


internal val BTTNetworkState.value:String
    get() {
        return when(this) {
            BTTNetworkState.Wifi -> "wifi"
            BTTNetworkState.Cellular -> "cellular"
            BTTNetworkState.Ethernet -> "ethernet"
            BTTNetworkState.Offline -> "offline"
            else -> ""
        }
    }

fun getNumberOfCPUCores() = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
    Os.sysconf(OsConstants._SC_NPROCESSORS_CONF)
} else {
    null
}