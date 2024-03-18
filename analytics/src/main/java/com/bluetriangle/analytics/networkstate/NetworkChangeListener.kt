package com.bluetriangle.analytics.networkstate

import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build.VERSION_CODES.LOLLIPOP
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(LOLLIPOP)
internal class NetworkChangeListener(
    private val logger: Logger?,
    private val state: BTTNetworkState
) :
    NetworkCallback() {

    private val networkType:String = state.name.uppercase()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        logger?.debug("NetworkMonitor: $networkType - Online")
        _isOnline.value = true
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        logger?.debug("NetworkMonitor: $networkType - Offline")
        _isOnline.value = false
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        logger?.debug("NetworkMonitor: $networkType Capabilities Changed")
    }

    private val _isOnline = MutableStateFlow(false)

    val isConnected: StateFlow<Boolean>
        get() = _isOnline

}