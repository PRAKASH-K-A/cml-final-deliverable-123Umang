package com.lab.fix;

import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {

    private static AtomicLong totalLatency = new AtomicLong(0);
    private static AtomicLong count = new AtomicLong(0);

    public static void recordLatency(long nanos) {
        totalLatency.addAndGet(nanos);
        long current = count.incrementAndGet();

        if (current % 1000 == 0) {
            double avgMicros = (totalLatency.get() / current) / 1000.0;
            System.out.println("Processed " + current +
                    " orders | Avg Latency: " + avgMicros + " us");
        }
    }
}