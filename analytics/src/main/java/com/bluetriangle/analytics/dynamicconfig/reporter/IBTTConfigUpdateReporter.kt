package com.bluetriangle.analytics.dynamicconfig.reporter

internal interface IBTTConfigUpdateReporter {

    fun reportSuccess()

    fun reportError(error: BTTConfigFetchError)

}