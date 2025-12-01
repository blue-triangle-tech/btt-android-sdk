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
    private val transports: IntArray,
) {
    private val _isOnline = MutableStateFlow(false)

    val isConnected: StateFlow<Boolean>
        get() = _isOnline

    fun setOnline(value: Boolean) {
        _isOnline.value = value
    }

    fun addTransportTypes(builder: NetworkRequest.Builder): NetworkRequest.Builder = builder
        .apply {
            transports.forEach { addTransportType(it) }
        }

    fun isSupported(capabilities: NetworkCapabilities): Boolean {
        return transports.any { capabilities.hasTransport(it) }
    }

}