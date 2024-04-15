package com.bluetriangle.analytics

import com.bluetriangle.analytics.caching.provider.PayloadProviderImpl
import com.bluetriangle.analytics.caching.classifier.ExtensionClassifier
import com.bluetriangle.analytics.caching.CacheLimitState
import com.bluetriangle.analytics.caching.MemoryLimitVerifier
import com.bluetriangle.analytics.caching.classifier.CacheType
import com.bluetriangle.analytics.caching.classifier.Classifier
import com.bluetriangle.analytics.caching.provider.PayloadComponent
import com.bluetriangle.analytics.caching.provider.PayloadProvider
import com.bluetriangle.analytics.caching.serializer.PayloadSerializer
import com.bluetriangle.analytics.caching.serializer.Serializer
import com.bluetriangle.analytics.utility.isDirectoryInvalid
import com.bluetriangle.analytics.utility.mb
import java.io.File
import java.io.IOException

class PayloadCache(private val configuration: BlueTriangleConfiguration) {

    companion object {
        val pickOrder = listOf(CacheType.Analytics, CacheType.Error, CacheType.Other, CacheType.Wcd)
        val deleteOrder =
            listOf(CacheType.Wcd, CacheType.Other, CacheType.Analytics, CacheType.Error)
    }

    private var cacheExpiryDuration = configuration.cacheExpiryDuration

    private var cacheMemoryLimit = configuration.cacheMemoryLimit

    private val directory: File = File(configuration.cacheDirectory!!)
    private var maxSize = if (isDirectoryValid) {
        configuration.maxCacheItems
    } else {
        0
    }
    private val memoryLimitVerifier =
        MemoryLimitVerifier(configuration.logger, cacheMemoryLimit, directory)
    private val classifier: Classifier =
        ExtensionClassifier()
    private val serializer: Serializer =
        PayloadSerializer(configuration.logger, directory, classifier)
    private val cachePayloadProvider: PayloadProvider =
        PayloadProviderImpl(directory, serializer, cacheExpiryDuration)

    /**
     * Get the next cached payload to attempt resending, if any
     *
     * @return next payload to send or null if none
     */
    fun pickNext(): Payload? {
        clearExpired()
        val file = getNextFile(pickOrder) ?: return null
        val payload = readPayload(file)
        configuration.logger?.debug("Next cached payload file: ${file.name}, ${payload?.type}")
        if (!file.delete()) {
            configuration.logger?.error("error deleting next cached payload file ${file.name}")
        }
        return payload
    }

    private fun getNextFile(order: List<CacheType>): File? {
        var component: PayloadComponent? = null
        for (type in order) {
            component = cachePayloadProvider.getByType(type).minByOrNull { it.createdAt }
            if (component != null) {
                break
            }
        }
        return component?.file
    }

    /**
     * Check to see if there are any cached payloads that need to still be sent
     *
     * @return true if there are existing payload cache files, else false
     */
    fun hasCachedPayloads(): Boolean {
        return getAllCacheFiles(false).isNotEmpty()
    }

    /**
     * Clears the cache by deleting all cache files in the cache directory
     */
    fun clearCache() {
        for (file in getAllCacheFiles(false)) {
            if (!file.delete()) {
                configuration.logger?.error("Error deleting ${file.name} while clearing cache")
            }
        }
    }

    fun save(payload: Payload) {
        try {
            serializer.serialize(payload)
        } catch (e: IOException) {
            configuration.logger?.error(e, "Failed to cache payload ${payload.id}")
        }
        cleanUp()
    }

    private fun readPayload(file: File): Payload? {
        try {
            return serializer.deserialize(file)
        } catch (e: IOException) {
            configuration.logger?.error(e, "Failed to load payload ${file.absolutePath}")
            if (!file.delete()) {
                configuration.logger?.warn("Could not delete payload file ${file.absolutePath}")
            }
        }
        return null
    }

    /**
     * Get all cache files, sorted oldest to newest
     *
     * @return all cached
     */
    private fun getAllCacheFiles(sort: Boolean): List<File> {
        if (isDirectoryValid) {
            // lets filter the session.json here
            val files = directory.listFiles()?.toList() ?: emptyList()
            if (sort) {
                return files.sortedBy { it.lastModified() }
            }
            return files
        }
        return emptyList()
    }

    /**
     * Check if a dir. is valid and have write and read permission
     *
     * @return true if valid and has permissions or false otherwise
     */
    private val isDirectoryValid: Boolean
        get() {
            if (directory.isDirectoryInvalid) {
                configuration.logger?.error("The directory for caching files is not valid: ${directory.absolutePath}")
                return false
            }
            return true
        }

    /**
     * Rotates the caching folder if full, deleting the oldest files first
     */
    private fun cleanUp() {
        clearExpired()
        var limitReached = memoryLimitVerifier.isLimitReached()
        while (limitReached is CacheLimitState.Reached) {
            configuration.logger?.warn("Cache memory limit exceeded by ${limitReached.exceededBytes.mb}MB! Deleting files")
            val fileToDelete = getNextFile(deleteOrder)
            if (fileToDelete == null || !fileToDelete.delete()) break
            limitReached = memoryLimitVerifier.isLimitReached()
        }
    }

    private fun clearExpired() {
        cachePayloadProvider.getExpired().forEach {
            it.file.delete()
        }
    }
}