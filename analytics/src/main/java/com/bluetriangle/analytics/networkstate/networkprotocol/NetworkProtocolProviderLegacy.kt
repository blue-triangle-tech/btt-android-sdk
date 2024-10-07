package com.bluetriangle.analytics.networkstate.networkprotocol

import android.os.Build
import android.util.Log
import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

internal class NetworkProtocolProviderLegacy : NetworkProtocolProvider {

    init {
        Log.d("BlueTriangle", "Build Version: ${Build.VERSION.SDK_INT}")
    }

    override val networkProtocol: StateFlow<NetworkProtocolInfo>
        get() = flowOf(NetworkProtocolInfo(BTTNetworkProtocol.Unknown)).stateIn(
            GlobalScope,
            SharingStarted.WhileSubscribed(),
            NetworkProtocolInfo(BTTNetworkProtocol.Unknown)
        )
}