/*
 * Copyright (c) 2024, Blue Triangle
 * All rights reserved.
 *
 */
package com.bluetriangle.analytics.dynamicconfig.reporter

internal sealed class BTTConfigFetchError(val reason:String) {
    class ErrorResponse(responseCode: Int):BTTConfigFetchError("API Returned $responseCode Response Code")
    class InvalidJSON(reason:String):BTTConfigFetchError(reason)
    class Other(reason:String):BTTConfigFetchError(reason)
}