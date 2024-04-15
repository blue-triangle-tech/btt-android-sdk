package com.bluetriangle.analytics.networkstate.data

import com.bluetriangle.analytics.networkstate.BTTNetworkState

internal data class NetworkSliceStats(
    val from: Long,
    val to: Long
) {
    var wifi: Long = 0
    var cellular: Long = 0
    var ethernet: Long = 0
    var offline: Long = 0

    fun add(state: BTTNetworkState, milliseconds: Long) {
        when (state) {
            BTTNetworkState.Wifi -> wifi += milliseconds
            BTTNetworkState.Cellular -> cellular += milliseconds
            BTTNetworkState.Offline -> offline += milliseconds
            BTTNetworkState.Ethernet -> ethernet += milliseconds
            else -> {}
        }
    }
}