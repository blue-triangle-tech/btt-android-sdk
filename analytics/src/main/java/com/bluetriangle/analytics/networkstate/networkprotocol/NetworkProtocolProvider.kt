package com.bluetriangle.analytics.networkstate.networkprotocol

import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import kotlinx.coroutines.flow.StateFlow

internal class NetworkProtocolInfo(
    val protocol: BTTNetworkProtocol,
    val source: Int? = null
)

internal interface NetworkProtocolProvider {

    val networkProtocol: StateFlow<NetworkProtocolInfo>

}