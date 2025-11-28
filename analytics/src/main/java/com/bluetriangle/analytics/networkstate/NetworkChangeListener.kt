package com.bluetriangle.analytics.networkstate

import android.annotation.SuppressLint
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build.VERSION_CODES.LOLLIPOP
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.utility.value

@RequiresApi(LOLLIPOP)
@SuppressLint("MissingPermission")
internal class NetworkChangeListener(
    private val logger: Logger?,
    private val handlers: Array<NetworkTransportHandler>
) : NetworkCallback() {

    private val networkMap = mutableMapOf<Network, NetworkCapabilities?>()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        logger?.debug("NetworkMonitor: onAvailable")
        networkMap.put(network, null)
    }

    private fun refreshNetworkState() {
        val handlerMap = handlers.associateWith { false }.toMutableMap()
        for(network in networkMap) {
            network.value?.let { capabilities ->
                for(handler in handlers) {
                    if(handler.isNetworkOnline(capabilities)) {
                        handlerMap[handler] = true
                    }
                }
            }
        }
        for(handler in handlerMap) {
            handler.key.setOnline(handler.value)
            logger?.debug("NetworkMonitor: ${handler.key.state.value} - ${if(handler.value) "Online" else "Offline"}")
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        logger?.debug("NetworkMonitor: onLost")
        networkMap.remove(network)
        refreshNetworkState()
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        logger?.debug("NetworkMonitor: Capabilities Changed")
        networkMap[network] = networkCapabilities
        refreshNetworkState()
    }

}