package com.bluetriangle.analytics.dynamicconfig.model

class MissingFieldException(val missingFields: Array<String>) : Exception("Missing fields: ${missingFields.contentToString()}")