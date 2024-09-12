package com.bluetriangle.analytics

import com.bluetriangle.analytics.caching.classifier.CacheType
import java.util.*

data class Payload(
    val id: String = UUID.randomUUID().toString(), // The UUID id for this payload, used in cache filename
    val payloadAttempts: Int = 0, // The number of attempts this payload has been tried to be sent
    val url: String, // The URL to send this payload
    val data: String, // The actual payload to send, base64 encoded,
    val type: CacheType = CacheType.Other,
    val createdAt:Long
)