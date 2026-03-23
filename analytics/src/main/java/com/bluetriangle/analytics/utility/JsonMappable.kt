package com.bluetriangle.analytics.utility

import org.json.JSONObject

interface JsonMappable {
    fun toJson(): JSONObject
}

interface JsonWritable {
    fun writeTo(jsonObject: JSONObject)
}