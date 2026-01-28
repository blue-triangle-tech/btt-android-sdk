package com.bluetriangle.analytics.model

data class ANRRecord(
    val time: Long,
    val stackTrace: Array<StackTraceElement>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ANRRecord) return false

        if (time != other.time) return false
        if (!stackTrace.contentEquals(other.stackTrace)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + stackTrace.contentHashCode()
        return result
    }
}