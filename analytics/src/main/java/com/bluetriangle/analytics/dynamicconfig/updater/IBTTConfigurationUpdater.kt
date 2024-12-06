/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.updater

internal interface IBTTConfigurationUpdater {

    suspend fun update()

    suspend fun forceUpdate()

}