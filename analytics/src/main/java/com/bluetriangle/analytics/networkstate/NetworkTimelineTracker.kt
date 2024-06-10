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
    private val networkSwitches = arrayListOf<NetworkSwitch>()

    init {
        appScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                networkStateMonitor.state.collectLatest(::onNetworkChange)
            }
        }
    }

    @Synchronized
    private fun onNetworkChange(network: BTTNetworkState) {
        Tracker.instance?.configuration?.logger?.info("Network change received: ${network.name}")
        val timestamp = System.currentTimeMillis()
        // if this is not the first network state, there will be other network switch info in the array.
        // get the last one from that and set it's end time to current time
        if(networkSwitches.isNotEmpty()) {
            val lastSwitch = networkSwitches[networkSwitches.lastIndex]
            lastSwitch.endTimestamp = timestamp
        }
        networkSwitches.add(NetworkSwitch(network, timestamp))
    }

    /**
     * @param from the start timestamp (in milliseconds) of the duration for which Network state data to be fetched
     * @param to the end timestamp (in milliseconds) of the duration for which Network state data to be fetched
     * @return NetworkSliceStats that tells the amount of time the device was in wifi, ethernet, cellular, offline states respectively during the duration between [from] and [to].
     */
    @Synchronized
    fun sliceStats(from:Long, to:Long):NetworkSliceStats {
        val networkStats = NetworkSliceStats(from, to)
        // Get the snapshot of the network state switch data for the entire Tracker session till this moment
        val networkSwitchesSnapshot = ArrayList(networkSwitches)

        // iterate over each of them to see which network state data falls under the duration [from] and [to]
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