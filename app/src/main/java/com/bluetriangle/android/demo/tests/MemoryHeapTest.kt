package com.bluetriangle.android.demo.tests

class MemoryHeapTest(
    val interval: Long = 30L
) : BTTTestCase {
    override val title: String
        get() = "Memory Heap Test"
    override val description: String
        get() = "Performs memory allocation on the heap for $interval secs"

    override fun run(): String? {
        val memoryToAllocate = 20 * 1024 * 1024
        val allocatedMemory = ByteArray(memoryToAllocate)
        allocatedMemory.fill(0)
        Thread.sleep(interval * 1000)
        allocatedMemory.fill(2)
        return null
    }
}