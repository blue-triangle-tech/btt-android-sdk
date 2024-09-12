package com.bluetriangle.analytics

data class PerformanceReport(
    private val minMemory: Long,
    private val maxMemory: Long,
    private val averageMemory: Long,
    private val minCpu: Double,
    private val maxCpu: Double,
    private val averageCpu: Double,
    private val maxMainThreadBlock: Long
) {
    val timerFields: Map<String, String>
        get() {
            return mapOf(
                FIELD_MIN_CPU to minCpu.toString(),
                FIELD_MAX_CPU to maxCpu.toString(),
                FIELD_AVG_CPU to averageCpu.toString(),
                FIELD_MIN_MEMORY to minMemory.toString(),
                FIELD_MAX_MEMORY to maxMemory.toString(),
                FIELD_AVG_MEMORY to averageMemory.toString(),
            )
        }

    override fun toString(): String {
        return "PerformanceReport{minMemory=$minMemory, maxMemory=$maxMemory, averageMemory=$averageMemory, minCpu=$minCpu, maxCpu=$maxCpu, averageCpu=$averageCpu}"
    }

    companion object {
        const val FIELD_MIN_CPU = "minCPU"
        const val FIELD_MAX_CPU = "maxCPU"
        const val FIELD_AVG_CPU = "avgCPU"
        const val FIELD_MIN_MEMORY = "minMemory"
        const val FIELD_TOTAL_MEMORY = "totalMemory"
        const val FIELD_MAX_MEMORY = "maxMemory"
        const val FIELD_AVG_MEMORY = "avgMemory"
        const val FIELD_MAX_MAIN_THREAD_BLOCK = "maxMainThreadBlock"
    }
}