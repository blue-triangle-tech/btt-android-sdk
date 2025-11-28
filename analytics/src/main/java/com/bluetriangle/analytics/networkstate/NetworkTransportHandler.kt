package com.bluetriangle.analytics.networkstate

import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class NetworkTransportHandler(
    val state: BTTNetworkState,
    val transports: IntArray,
) {
    private val _isOnline = MutableStateFlow(false)

    val isConnected: StateFlow<Boolean>
        get() = _isOnline

    fun setOnline(value: Boolean) {
        _isOnline.value = value
    }

    fun applyNetworkCapabilities(builder: NetworkRequest.Builder): NetworkRequest.Builder = builder
        .apply {
            transports.forEach { addTransportType(it) }
        }
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    fun isNetworkOnline(capabilities: NetworkCapabilities): Boolean {
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val hasTransport = transports.any { capabilities.hasTransport(it) }
        return hasInternet && hasTransport
    }

}