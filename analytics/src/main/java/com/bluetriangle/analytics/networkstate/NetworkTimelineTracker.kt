package com.bluetriangle.analytics.networkstate

import android.os.Build
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.networkstate.data.NetworkSliceStats
import com.bluetriangle.analytics.networkstate.data.NetworkSwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class NetworkTimelineTracker(private val networkStateMonitor: NetworkStateMonitor) {

    private var appScope = CoroutineScope(Dispatchers.IO)

    init {
        appScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                networkStateMonitor.state.collectLatest(::onNetworkChange)
            }
        }
    }

    private val networkSwitches = arrayListOf<NetworkSwitch>()

    private fun onNetworkChange(network: BTTNetworkState) {
        Tracker.instance?.configuration?.logger?.debug("BTTNetworkStateChange: ${network.name}")
        val timestamp = System.currentTimeMillis()
        if(networkSwitches.isNotEmpty()) {
            val lastSwitch = networkSwitches[networkSwitches.lastIndex]
            lastSwitch.endTimestamp = timestamp
        }
        networkSwitches.add(NetworkSwitch(network, timestamp))
    }

    fun sliceStats(from:Long, to:Long):NetworkSliceStats {
        val networkStats = NetworkSliceStats(from, to)
        val networkSwitchesSnapshot = ArrayList(networkSwitches)
        for(switch in networkSwitchesSnapshot) {
            if(switch.overlaps(from, to)) {
                val switchStart = switch.startTimestamp
                val switchEnd = if(switch.endTimestamp == null) to else switch.endTimestamp!!
                networkStats.add(switch.toState, switchEnd.coerceAtMost(to) - switchStart.coerceAtLeast(from))
            }
        }
        return networkStats
    }
}