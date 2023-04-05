package com.bluetriangle.analytics.payload

import com.bluetriangle.analytics.BlueTriangleConfiguration
import java.io.File
import java.io.IOException

class PayloadCache(private val configuration: BlueTriangleConfiguration) {
    private val directory: File = File(configuration.cacheDirectory!!)
    private var maxSize = if (isDirectoryValid) { configuration.maxCacheItems } else { 0 }

    /**
     * Get the next cached payload to attempt resending, if any
     *
     * @return next payload to send or null if none
     */
    val nextCachedPayload: Payload?
        get() {
            val files = getAllCacheFiles(true)
            if (files.isNotEmpty()) {
                val file = files.first()
                val payload = readPayload(file)
                if (!file.delete()) {
                    configuration.logger?.error("error deleting next cached payload file ${file.name}")
                }
                return payload
            }
            return null
        }

    /**
     * Check to see if there are any cached payloads that need to still be sent
     *
     * @return true if there are existing payload cache files, else false
     */
    fun hasCachedPayloads(): Boolean {
        return if (maxSize > 0) {
            getAllCacheFiles(false).isNotEmpty()
        } else false
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

    fun cachePayload(payload: Payload) {
        if (maxSize > 0) {
            if (payload.payloadAttempts >= configuration.maxAttempts) {
                configuration.logger?.warn("Payload ${payload.id} has exceeded max attempts ${payload.payloadAttempts}")
                return
            }
            try {
                payload.serialize(directory)
            } catch (e: IOException) {
                configuration.logger?.error(e, "Failed to cache payload ${payload.id}")
            }
            rotateCacheIfNeeded()
        }
    }

    private fun readPayload(file: File): Payload? {
        try {
            return Payload.deserialize(file)
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
            if (!directory.isDirectory || !directory.canWrite() || !directory.canRead()) {
                configuration.logger?.error("The directory for caching files is not valid: ${directory.absolutePath}")
                return false
            }
            return true
        }

    /**
     * Rotates the caching folder if full, deleting the oldest files first
     */
    private fun rotateCacheIfNeeded() {
        val files = getAllCacheFiles(true)
        if (files.size >= maxSize) {
            configuration.logger?.warn("Cache folder size ${files.size} > $maxSize. Rotating files.")
            val totalToBeDeleted = files.size - maxSize + 1
            val filesToDelete = files.take(totalToBeDeleted)
            for (file in filesToDelete) {
                if (!file.delete()) {
                    configuration.logger?.warn("Error deleting file: ${file.absolutePath}")
                }
            }
        }
    }
}