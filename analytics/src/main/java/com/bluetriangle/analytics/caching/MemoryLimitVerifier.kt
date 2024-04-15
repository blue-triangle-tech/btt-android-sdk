package com.bluetriangle.analytics.caching

import com.bluetriangle.analytics.Logger
import com.bluetriangle.analytics.utility.isDirectoryInvalid
import java.io.File
import kotlin.math.abs

sealed class CacheLimitState {
    class Reached(val exceededBytes: Long) : CacheLimitState()
    class NotReached(val remainingBytes: Long) : CacheLimitState()

}

class MemoryLimitVerifier(
    logger: Logger?,
    private val memoryLimit: Long,
    private val directory: File
) {

    fun isLimitReached(): CacheLimitState {
        if(directory.isDirectoryInvalid) return CacheLimitState.NotReached(0)

        val files = directory.listFiles()
        val totalSize = files?.sumOf(::calculateSize) ?: 0L
        val difference = abs(memoryLimit - totalSize)

        return if (totalSize < memoryLimit) {
            CacheLimitState.NotReached(difference)
        } else {
            CacheLimitState.Reached(difference)
        }
    }

    private fun calculateSize(file: File?): Long {
        return (file?.readBytes()?.size ?: 0).toLong()
    }

}