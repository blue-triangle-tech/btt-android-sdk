package com.bluetriangle.analytics.caching.provider

import com.bluetriangle.analytics.caching.classifier.CacheType
import com.bluetriangle.analytics.caching.serializer.Serializer
import com.bluetriangle.analytics.utility.isDirectoryInvalid
import java.io.File

class PayloadProviderImpl(
    private val directory: File,
    private val serializer: Serializer,
    private val expiryDuration: Long
) : PayloadProvider {

    private fun getAll(): List<PayloadComponent> {
        if (directory.isDirectoryInvalid) return emptyList()

        return directory.listFiles()?.map {
            val payload = serializer.deserialize(it)?:return@map null
            PayloadComponent(it, payload.createdAt, payload.type)
        }?.filterNotNull() ?: emptyList()
    }

    override fun getByType(cacheType: CacheType): List<PayloadComponent> {
        return getAll().filter {
            it.type == cacheType
        }
    }

    override fun getExpired(): List<PayloadComponent> {
        val currentTime = System.currentTimeMillis()
        return getAll().filter {
            (currentTime - it.createdAt) >= expiryDuration
        }
    }

}