package com.bluetriangle.analytics.caching.classifier

import java.io.File

enum class CacheType(val extension: String) {
    Analytics("ana"),
    Error("err"),
    Wcd("wcd"),
    Other("")
}

interface Classifier {
    fun classify(file: File): CacheType

}