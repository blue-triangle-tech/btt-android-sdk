package com.bluetriangle.analytics.networkstate.networkprotocol

import android.content.Context
import android.os.Build
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.S)
internal class NetworkTypeProviderAPI31Impl(val context: Context):NetworkTypeProvider {

    override val networkType: Flow<Int?> =
        callbackFlow {
            val telephonyManager =
                ContextCompat.getSystemService(context, TelephonyManager::class.java)
                    ?: return@callbackFlow

            val exec = Executors.newSingleThreadExecutor()

            val callback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener {
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    trySend(telephonyDisplayInfo.networkType)
                }
            }
            telephonyManager.registerTelephonyCallback(exec, callback)

            awaitClose {
                telephonyManager.unregisterTelephonyCallback(callback)
                exec.shutdown()
            }
        }

}