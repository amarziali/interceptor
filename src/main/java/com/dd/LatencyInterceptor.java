package com.dd;

import datadog.trace.api.DDTags;
import datadog.trace.api.interceptor.MutableSpan;
import datadog.trace.api.interceptor.TraceInterceptor;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
        final Object urlTag = rootSpan.getTag("http.url");
        if (urlTag != null) {
            try {
                final String query = new URL(urlTag.toString()).getQuery();
                if (query != null) {
                    for (final String entry : query.split("&")) {
                        final String[] keyValuePair = entry.split("=", 2);
                        final String name = URLDecoder.decode(keyValuePair[0], "UTF-8");
                        if ("userid".equals(name) || "TrUserId".equals(name)) {
                            final String value = keyValuePair.length > 1 ? URLDecoder.decode(keyValuePair[1], "UTF-8") : "";
                            rootSpan.setTag(DDTags.USER_NAME, value);
                            break;
                        }
                    }
                }

            } catch (MalformedURLException | UnsupportedEncodingException e) {
                // just swallow (or log if needed)
            }
        }
        return latencyTrace;
    }

    @Override
    public int priority() {
        // some high unique number so this interceptor is last
        return 100;
    }
}
