package com.bluetriangle.analytics.networkstate.networkprotocol

import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import com.bluetriangle.analytics.networkstate.data.networkProtocol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class NetworkProtocolProviderAPI31Impl(
    networkTypeProvider: NetworkTypeProvider
):NetworkProtocolProvider {

    override val networkProtocol:StateFlow<NetworkProtocolInfo> = networkTypeProvider.networkType.map {
        NetworkProtocolInfo((it?.networkProtocol ?: BTTNetworkProtocol.Unknown), it)
    }.stateIn(
        GlobalScope,
        SharingStarted.WhileSubscribed(),
        NetworkProtocolInfo(BTTNetworkProtocol.Unknown)
    )
}