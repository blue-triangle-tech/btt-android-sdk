package com.bluetriangle.analytics.networkstate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.utility.cellularTransports
import com.bluetriangle.analytics.utility.ethernetTransports
import com.bluetriangle.analytics.utility.wifiTransports
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


internal enum class BTTNetworkState {
    Wifi,
    Cellular,
    Ethernet,
    Other,
    Offline
}

internal interface INetworkStateMonitor {
    val state: StateFlow<BTTNetworkState>
}

@SuppressLint("MissingPermission")
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class NetworkStateMonitor(logger: Logger?, context: Context) : INetworkStateMonitor {

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager

    @SuppressLint("InlinedApi")
    private val wifiTransportHandler = NetworkTransportHandler(
        wifiTransports,
        logger,
        BTTNetworkState.Wifi
    )

    private val cellularTransportHandler = NetworkTransportHandler(
        cellularTransports,
        logger,
        BTTNetworkState.Cellular
    )

    private val ethernetTransportHandler = NetworkTransportHandler(
        ethernetTransports,
        logger,
        BTTNetworkState.Ethernet
    )

    override val state = combine(
        wifiTransportHandler.isConnected,
        cellularTransportHandler.isConnected,
        ethernetTransportHandler.isConnected,
    ) { wifi, cellular, ethernet ->
        when {
            wifi -> BTTNetworkState.Wifi
            ethernet -> BTTNetworkState.Ethernet
            cellular -> BTTNetworkState.Cellular
            else -> BTTNetworkState.Offline
        }
    }.stateIn(GlobalScope, SharingStarted.Eagerly, BTTNetworkState.Offline)

    init {
        arrayOf(wifiTransportHandler, cellularTransportHandler, ethernetTransportHandler).forEach {
            connectivityManager?.registerNetworkCallback(
                it.networkRequest,
                it.listener
            )
        }
        logger?.debug("-------------------Network Monitoring Started!-----------------")
    }

}