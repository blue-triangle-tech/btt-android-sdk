package com.bluetriangle.analytics;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

final class PerformanceReport {

    public static final String FIELD_MIN_CPU = "minCPU";
    public static final String FIELD_MAX_CPU = "maxCPU";
    public static final String FIELD_AVG_CPU = "avgCPU";
    public static final String FIELD_MIN_MEMORY = "minMemory";
    public static final String FIELD_MAX_MEMORY = "maxMemory";
    public static final String FIELD_AVG_MEMORY = "avgMemory";

    private final long minMemory;
    private final long maxMemory;
    private final long averageMemory;

    private final double minCpu;
    private final double maxCpu;
    private final double averageCpu;

    public PerformanceReport(long minMemory, long maxMemory, long averageMemory, double minCpu, double maxCpu,
            double averageCpu) {
        this.minMemory = minMemory;
        this.maxMemory = maxMemory;
        this.averageMemory = averageMemory;
        this.minCpu = minCpu;
        this.maxCpu = maxCpu;
        this.averageCpu = averageCpu;
    }

    public Map<String, String> getTimerFields() {
        final HashMap<String, String> fields = new HashMap<>(6);
        fields.put(FIELD_MIN_CPU, Double.toString(minCpu));
        fields.put(FIELD_MAX_CPU, Double.toString(maxCpu));
        fields.put(FIELD_AVG_CPU, Double.toString(averageCpu));
        fields.put(FIELD_MIN_MEMORY, Long.toString(minMemory));
        fields.put(FIELD_MAX_MEMORY, Long.toString(maxMemory));
        fields.put(FIELD_AVG_MEMORY, Long.toString(averageMemory));
        return fields;
    }

    @NonNull
    @Override
    public String toString() {
        return "PerformanceReport{" +
                "minMemory=" + minMemory +
                ", maxMemory=" + maxMemory +
                ", averageMemory=" + averageMemory +
                ", minCpu=" + minCpu +
                ", maxCpu=" + maxCpu +
                ", averageCpu=" + averageCpu +
                '}';
    }
}
