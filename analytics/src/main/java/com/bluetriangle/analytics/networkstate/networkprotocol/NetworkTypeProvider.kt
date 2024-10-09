package com.bluetriangle.analytics.networkstate.networkprotocol

import kotlinx.coroutines.flow.Flow

internal interface NetworkTypeProvider {
    val networkType:Flow<Int?>
}