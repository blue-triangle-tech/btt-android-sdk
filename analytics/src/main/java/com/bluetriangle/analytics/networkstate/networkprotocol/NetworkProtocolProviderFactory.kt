package com.bluetriangle.analytics.networkstate.networkprotocol

import android.content.Context
import android.os.Build

internal object NetworkProtocolProviderFactory {

    internal fun getNetworkProtocolProvider(context: Context): NetworkProtocolProvider = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> NetworkProtocolProviderAPI31Impl(
            NetworkTypeProviderAPI31Impl(context)
        )

        else -> NetworkProtocolProviderLegacy()
    }

}