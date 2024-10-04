package com.bluetriangle.analytics.networkstate.networkprotocol

import android.telephony.PhoneStateListener
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
@RequiresApi(Build.VERSION_CODES.R)
class NetworkTypeProviderAPI30Impl(val context: Context):NetworkTypeProvider {

    override val networkType: Flow<Int?> =
        callbackFlow {
            val telephonyManager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
                ?: return@callbackFlow

            val listener = object : PhoneStateListener() {
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    trySend(telephonyDisplayInfo.networkType)
                }
            }
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED)

            awaitClose {
                telephonyManager.listen(listener, 0)
            }
        }
}