package com.bluetriangle.analytics.dynamicconfig.reporter

internal sealed class BTTConfigFetchError(val reason:String) {
    class ErrorResponse(responseCode: Int):BTTConfigFetchError("API Returned $responseCode Response Code")
    class InvalidJSON(reason:String):BTTConfigFetchError(reason)
    class FieldMissing(missingFields: Array<String>):BTTConfigFetchError("Config JSON missing fields: ${missingFields.contentToString()}")
    class Other(reason:String):BTTConfigFetchError(reason)
}