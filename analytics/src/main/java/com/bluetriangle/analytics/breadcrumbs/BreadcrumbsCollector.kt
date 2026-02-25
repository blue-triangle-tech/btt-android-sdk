package com.bluetriangle.analytics.breadcrumbs

internal class BreadcrumbsCollector (
    private val capacity: Int
) {

    init {
        require(capacity > 0) { "Capacity must be > 0" }
    }

    private val buffer = arrayOfNulls<BreadcrumbEvent>(capacity)
    private var head = 0
    private var size = 0

    @Synchronized
    fun add(item: BreadcrumbEvent) {
        buffer[head] = item
        head = (head + 1) % capacity
        if (size < capacity) {
            size++
        }
    }

    @Synchronized
    fun snapshot(): List<BreadcrumbEvent> {
        val result = ArrayList<BreadcrumbEvent>(size)

        val start = if (size == capacity) head else 0

        repeat(size) { i ->
            val index = (start + i) % capacity
            buffer[index]?.let { result.add(it) }
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