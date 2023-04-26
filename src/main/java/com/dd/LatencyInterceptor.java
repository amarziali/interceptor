package com.dd;

import datadog.trace.api.DDTags;
import datadog.trace.api.interceptor.MutableSpan;
import datadog.trace.api.interceptor.TraceInterceptor;

import java.util.Collection;

public class LatencyInterceptor implements TraceInterceptor {
    @Override
    public Collection<? extends MutableSpan> onTraceComplete(
            Collection<? extends MutableSpan> latencyTrace) {
        if (latencyTrace.isEmpty()) {
            return latencyTrace;
        }
        MutableSpan rootSpan = latencyTrace.iterator().next().getLocalRootSpan();
        long latency = rootSpan.getDurationNano();
        if (latency > 100000000) { // 100 ms or any other custom logic to keep a trace based on root span properties
            rootSpan.setTag(DDTags.MANUAL_KEEP, true);
        }
        return latencyTrace;
    }

    @Override
    public int priority() {
        // some high unique number so this interceptor is last
        return 100;
    }
}
