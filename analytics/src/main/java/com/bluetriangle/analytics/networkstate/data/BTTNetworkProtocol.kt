package com.bluetriangle.analytics.networkstate.data

import android.os.Parcelable
import android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT
import android.telephony.TelephonyManager.NETWORK_TYPE_CDMA
import android.telephony.TelephonyManager.NETWORK_TYPE_EDGE
import android.telephony.TelephonyManager.NETWORK_TYPE_EHRPD
import android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0
import android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A
import android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B
import android.telephony.TelephonyManager.NETWORK_TYPE_GPRS
import android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA
import android.telephony.TelephonyManager.NETWORK_TYPE_HSPA
import android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP
import android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA
import android.telephony.TelephonyManager.NETWORK_TYPE_IDEN
import android.telephony.TelephonyManager.NETWORK_TYPE_LTE
import android.telephony.TelephonyManager.NETWORK_TYPE_NR
import android.telephony.TelephonyManager.NETWORK_TYPE_UMTS
import kotlinx.parcelize.Parcelize

@Suppress("EnumEntryName")
@Parcelize
internal enum class BTTNetworkProtocol(val description: String) : Parcelable {
    _2G("2g"),
    _3G("3g"),
    _4G("4g"),
    _5G("5g"),
    Unknown("")
}

internal val Int.networkProtocol: BTTNetworkProtocol
    get() {
        val type2G = listOf(
            NETWORK_TYPE_GPRS,
            NETWORK_TYPE_EDGE,
            NETWORK_TYPE_CDMA,
            NETWORK_TYPE_1xRTT,
            NETWORK_TYPE_IDEN
        )
        val type3G = listOf(
            NETWORK_TYPE_UMTS,
            NETWORK_TYPE_EVDO_0,
            NETWORK_TYPE_EVDO_A,
            NETWORK_TYPE_HSDPA,
            NETWORK_TYPE_HSUPA,
            NETWORK_TYPE_HSPA,
            NETWORK_TYPE_EVDO_B,
            NETWORK_TYPE_EHRPD,
            NETWORK_TYPE_HSPAP
        )

        return when (this) {
            in type2G -> BTTNetworkProtocol._2G
            in type3G -> BTTNetworkProtocol._3G
            NETWORK_TYPE_LTE -> BTTNetworkProtocol._4G
            NETWORK_TYPE_NR -> BTTNetworkProtocol._5G
            else -> BTTNetworkProtocol.Unknown
        }
    }