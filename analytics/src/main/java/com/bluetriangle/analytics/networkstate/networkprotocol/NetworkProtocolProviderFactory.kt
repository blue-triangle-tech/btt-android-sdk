package com.bluetriangle.analytics.networkstate.networkprotocol

import android.content.Context
import android.os.Build

internal object NetworkProtocolProviderFactory {

    internal fun getNetworkProtocolProvider(context: Context): NetworkProtocolProvider = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.S -> NetworkProtocolProviderAPI30Impl(
            NetworkTypeProviderAPI31Impl(context)
        )

        Build.VERSION.SDK_INT > Build.VERSION_CODES.R -> NetworkProtocolProviderAPI30Impl(
            NetworkTypeProviderAPI30Impl(context)
        )

        else -> NetworkProtocolProviderLegacy()
    }
}