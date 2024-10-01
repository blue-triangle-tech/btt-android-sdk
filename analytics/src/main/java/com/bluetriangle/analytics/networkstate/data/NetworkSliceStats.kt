package com.bluetriangle.analytics.networkstate.data

import com.bluetriangle.analytics.networkstate.BTTNetworkProtocol
import com.bluetriangle.analytics.networkstate.BTTNetworkState

internal data class NetworkSliceStats(
    val from: Long,
    val to: Long
) {
    var wifi: Long = 0
    var cellular = hashMapOf<BTTNetworkProtocol, Long>()
    var ethernet: Long = 0
    var offline: Long = 0

    val sources = hashSetOf<String>()

    fun add(state: BTTNetworkState, milliseconds: Long) {
        when (state) {
            BTTNetworkState.Wifi -> wifi += milliseconds
            is BTTNetworkState.Cellular -> {
                cellular.merge(state.protocol, milliseconds, Long::plus)
                sources.add(state.source)
            }
            BTTNetworkState.Offline -> offline += milliseconds
            BTTNetworkState.Ethernet -> ethernet += milliseconds
            else -> {}
        }
    }
}