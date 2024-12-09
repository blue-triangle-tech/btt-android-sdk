/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.reporter

internal interface IBTTConfigUpdateReporter {

    fun reportSuccess()

    fun reportError(error: BTTConfigFetchError)

}