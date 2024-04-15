package com.bluetriangle.analytics.caching.provider

import com.bluetriangle.analytics.caching.classifier.CacheType
import java.io.File

class PayloadComponent(
    val file: File,
    val createdAt: Long,
    val type: CacheType
)