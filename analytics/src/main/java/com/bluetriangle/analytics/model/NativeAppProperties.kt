package com.bluetriangle.analytics.model

import android.os.Parcelable
import com.bluetriangle.analytics.Constants
import com.bluetriangle.analytics.Constants.APP_VERSION
import com.bluetriangle.analytics.Constants.FULL_TIME
import com.bluetriangle.analytics.Constants.LAUNCH_SCREEN_NAME
import com.bluetriangle.analytics.Constants.LOAD_TIME
import com.bluetriangle.analytics.Constants.MAX_MAIN_THREAD_USAGE
import com.bluetriangle.analytics.Constants.NETWORK_TYPE_CELLULAR
import com.bluetriangle.analytics.Constants.NETWORK_TYPE_ETHERNET
import com.bluetriangle.analytics.Constants.NETWORK_TYPE_OFFLINE
import com.bluetriangle.analytics.Constants.NETWORK_TYPE_WIFI
import com.bluetriangle.analytics.Constants.NUMBER_OF_CPU_CORES
import com.bluetriangle.analytics.Constants.SCREEN_TYPE
import com.bluetriangle.analytics.Constants.SDK_VERSION
import com.bluetriangle.analytics.Timer
import com.bluetriangle.analytics.deviceinfo.DeviceInfo
import com.bluetriangle.analytics.networkcapture.CapturedRequest
import com.bluetriangle.analytics.networkcapture.CapturedRequest.Companion.FIELD_DEVICE_MODEL
import com.bluetriangle.analytics.networkstate.BTTNetworkState
import com.bluetriangle.analytics.networkstate.data.BTTNetworkProtocol
import com.bluetriangle.analytics.utility.value
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
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
    var netStateSource: String? = null,
    var appVersion: String? = null,
    var sdkVersion: String? = null
) : Parcelable {

    internal var loadStartTime: Long = 0
    internal var loadEndTime: Long = 0
    internal var disappearTime: Long = 0
    internal var className: String = ""

    private val cellularTotal
        get() = cellular?.entries?.map { it.value }?.let {
            if(it.isEmpty()) 0L else it.reduce(Long::plus)
        }

    fun toJSONObject(): JSONObject {
        val obj = JSONObject()
        obj.put(LOAD_TIME, loadTime)
        obj.put(FULL_TIME, fullTime)
        obj.put(MAX_MAIN_THREAD_USAGE, maxMainThreadUsage)
        obj.put(SCREEN_TYPE, screenType?.value)
        obj.put(NUMBER_OF_CPU_CORES, numberOfCPUCores)
        obj.put(APP_VERSION, appVersion)
        obj.put(SDK_VERSION, sdkVersion)

        networkStates.apply {
            forEach { obj.put(it) }
            obj.putNetStateField(networkStates)
        }

        if(!netStateSource.isNullOrEmpty()) {
            obj.put(Timer.FIELD_NET_STATE_SOURCE, netStateSource)
        }

        obj.put(LAUNCH_SCREEN_NAME, launchScreenName)
        obj.put(FIELD_DEVICE_MODEL, deviceModel)

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
                NETWORK_TYPE_WIFI to wifi,
                NETWORK_TYPE_CELLULAR to cellularTotal,
                NETWORK_TYPE_ETHERNET to ethernet,
                NETWORK_TYPE_OFFLINE to offline
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
            if (max.first == NETWORK_TYPE_CELLULAR) {
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