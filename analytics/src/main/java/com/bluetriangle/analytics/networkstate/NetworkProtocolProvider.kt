package com.bluetriangle.analytics.networkstate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
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
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.concurrent.Executors

@Suppress("EnumEntryName")
internal enum class BTTNetworkProtocol(val description: String) {
    _2G("2G"),
    _3G("3G"),
    _4G("4G"),
    _5G("5G"),
    Unknown("")
}

internal class NetworkProtocolProvider(val context: Context) {

    @SuppressLint("MissingPermission")
    private val telephonyDisplayInfo: Flow<TelephonyDisplayInfo?> =
        callbackFlow {
            val telephonyManager =
                ContextCompat.getSystemService(context, TelephonyManager::class.java)
                    ?: return@callbackFlow

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listenTelephonyCallback(telephonyManager)
            } else @Suppress("DEPRECATION") {
                val permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                if (permissionState == PackageManager.PERMISSION_GRANTED) {
                    listenPhoneStateListener(telephonyManager)
                }
            }
        }.stateIn(GlobalScope, SharingStarted.WhileSubscribed(), null)

    private val networkType = telephonyDisplayInfo.map {
        it?.networkType
    }

    val networkProtocol = networkType.map {
        it to (it?.networkProtocol ?: BTTNetworkProtocol.Unknown)
    }

    private suspend fun ProducerScope<TelephonyDisplayInfo>.listenTelephonyCallback(telephonyManager: TelephonyManager) {
        // The thread Executor used to run the listener. This governs how threads are created and
        // reused. Here we use a single thread.
        val exec = Executors.newSingleThreadExecutor()

        val callback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener {
            override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                trySend(telephonyDisplayInfo)
            }
        }
        telephonyManager.registerTelephonyCallback(exec, callback)

        awaitClose {
            telephonyManager.unregisterTelephonyCallback(callback)
            exec.shutdown()
        }
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private suspend fun ProducerScope<TelephonyDisplayInfo>.listenPhoneStateListener(
        telephonyManager: TelephonyManager?
    ) {
        val listener = object : PhoneStateListener() {
            override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                trySend(telephonyDisplayInfo)
            }
        }
        telephonyManager?.listen(listener, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED)

        awaitClose {
            telephonyManager?.listen(listener, 0)
        }
    }

    private val Int.networkProtocol: BTTNetworkProtocol
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
}