package com.bluetriangle.analytics.caching.serializer

import com.bluetriangle.analytics.Payload
import java.io.File

interface Serializer {

    fun serialize(payload: Payload)

    fun deserialize(file: File):Payload?

}