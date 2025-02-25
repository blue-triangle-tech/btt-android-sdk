package com.bluetriangle.analytics.model

import android.os.Parcelable
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DEVICE_MODEL
import com.bluetriangle.analytics.networkstate.BTTNetworkState
import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import com.bluetriangle.analytics.utility.value
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
internal data class NativeAppProperties(
    var loadTime: Long? = null,
    var fullTime: Long? = null,
    var maxMainThreadUsage: Long? = null,
    var screenType: ScreenType? = null,
    var numberOfCPUCores: Long? = null,
    var wifi: Long? = null,
    var cellular: Map<BTTNetworkProtocol, Long>? = null,
    var ethernet: Long? = null,
    var offline: Long? = null,
    var launchScreenName: String? = null,
    var deviceModel: String? = null,
    var netStateSource: String? = null
) : Parcelable {

    private val cellularTotal
        get() = cellular?.entries?.map { it.value }?.let {
            if(it.isEmpty()) 0L else it.reduce(Long::plus)
        }

    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put("loadTime", loadTime)
        obj.put("fullTime", fullTime)
        obj.put("maxMainThreadUsage", maxMainThreadUsage)
        obj.put("screenType", screenType?.value)
        obj.put("numberOfCPUCores", numberOfCPUCores)

        networkStates.apply {
            forEach { obj.put(it) }
            obj.putNetStateField(networkStates)
        }

        if(!netStateSource.isNullOrEmpty()) {
            obj.put(Timer.FIELD_NET_STATE_SOURCE, netStateSource)
        }

        obj.put("launchScreenName", launchScreenName)
        obj.put(FIELD_DEVICE_MODEL, deviceModel)

        Tracker.instance?.thirdPartyConnectorManager?.nativeAppPayloadFields?.forEach {
            obj.put(it.key, it.value)
        }
        return obj
    }

    private fun getMaxCellularProtocol() = cellular
        ?.entries
        ?.maxByOrNull { it.value }
        ?.key
        ?: BTTNetworkProtocol.Unknown

    private val networkStates: Array<Pair<String, Long?>>
        get() {
            return arrayOf(
                "wifi" to wifi,
                "cellular" to cellularTotal,
                "ethernet" to ethernet,
                "offline" to offline
            )
        }

    fun JSONObject.put(pair: Pair<String, Long?>) {
        if (pair.second != null && pair.second != 0L) {
            put(pair.first, pair.second)
        }
    }

    private fun JSONObject.putNetStateField(netStateFields: Array<Pair<String, Long?>>) {
        val maxField = netStateFields.maxByOrNull { it.second ?: 0L }

        maxField?.let { max ->
            if (max.first == "cellular") {
                getMaxCellularProtocol().also { protocol ->
                    put(
                        CapturedRequest.FIELD_NETWORK_STATE,
                        BTTNetworkState.Cellular("", protocol).value
                    )
                }
            } else {
                put(CapturedRequest.FIELD_NETWORK_STATE, maxField.first)
            }
        }
    }

    fun add(deviceInfo: DeviceInfo) {
        deviceModel = deviceInfo.deviceModel
    }
}