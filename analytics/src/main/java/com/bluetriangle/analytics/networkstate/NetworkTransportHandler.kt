package com.bluetriangle.analytics.networkstate

import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.bluetriangle.analytics.Logger
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class NetworkTransportHandler(
    transports:IntArray,
    logger: Logger?,
    networkState: BTTNetworkState
) {

    val networkRequest: NetworkRequest = getNetworkRequestFor(transports)

    private val _listener = NetworkChangeListener(
        logger,
        networkState
    )
    val listener:NetworkCallback
        get() = _listener

    val isConnected: Flow<Boolean>
        get() = _listener.isConnected

    private fun getNetworkRequestFor(transports: IntArray) = NetworkRequest.Builder()
        .apply {
            transports.forEach { addTransportType(it) }
        }
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

}