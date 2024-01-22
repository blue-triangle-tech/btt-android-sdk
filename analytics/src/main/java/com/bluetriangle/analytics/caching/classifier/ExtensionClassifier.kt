package com.bluetriangle.analytics.caching.classifier

import java.io.File

class ExtensionClassifier : Classifier {

    override fun classify(file: File): CacheType {
        val fileName = file.name.split(".")
        if(fileName.size < 2) return CacheType.Other

        CacheType.values().forEach {
            if(fileName.last() == it.extension) {
                return@classify it
            }
        }
        return CacheType.Other
    }

}