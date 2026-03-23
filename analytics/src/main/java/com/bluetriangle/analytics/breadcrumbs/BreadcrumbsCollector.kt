package com.bluetriangle.analytics.breadcrumbs

import com.bluetriangle.analytics.Tracker
import org.json.JSONArray
import org.json.JSONObject

internal class BreadcrumbsCollector (
    private val capacity: Int
) {
    private val buffer = arrayOfNulls<JSONObject>(capacity)
    private var head = 0
    private var size = 0

    @Synchronized
    fun add(item: BreadcrumbEvent) {
        buffer[head] = item.toJson()
        Tracker.instance?.configuration?.logger?.verbose("Added breadcrumb: ${buffer[head].toString()}")
        head = (head + 1) % capacity
        if (size < capacity) {
            size++
        }
    }

    @Synchronized
    fun snapshot(): JSONArray {
        val result = JSONArray()

        val start = if (size == capacity) head else 0

        repeat(size) { i ->
            val index = (start + i) % capacity
            buffer[index]?.let { result.put(it) }
        }

        return result
    }

    @Synchronized
    fun clear() {
        head = 0
        size = 0
        buffer.fill(null)
    }

    @Synchronized
    fun currentSize(): Int = size

    fun capacity(): Int = capacity
}