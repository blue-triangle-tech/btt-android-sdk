package com.bluetriangle.analytics.networkstate.data

import com.bluetriangle.analytics.networkstate.BTTNetworkState

internal data class NetworkSwitch(
    val toState: BTTNetworkState,
    val startTimestamp: Long
) {
    var endTimestamp: Long? = null

    fun overlaps(timestampFrom: Long, timestampTo: Long): Boolean {
        val start = startTimestamp
        val end = endTimestamp ?: return start <= timestampFrom || timestampTo > start

        /**
         *  In Inside:
         *  timestsamp             -------------------------------
         *  networkswitch   ----------------------------------------------
         *
         *  Is Before:
         *  timestsamp      -----------------------
         *  networkswitch          ---------------------------------------
         *
         *  Is After:
         *  timestsamp                        ----------------------------
         *  networkswitch   ---------------------------
         *
         *  Is Outside:
         *  timestsamp      ---------------------------------------------------
         *  networkswitch               ---------------------------
         */
        val isInside = start <= timestampFrom && timestampTo <= end;
        val isBefore = start in (timestampFrom + 1) until timestampTo && timestampTo <= end;
        val isAfter = timestampFrom in (start + 1) until end && timestampTo > end;
        val isOutside = timestampFrom <= start  && end <= timestampTo
        return isInside || isBefore || isAfter || isOutside;
    }
}