package com.dd;

import datadog.trace.api.DDTags;
import datadog.trace.api.interceptor.MutableSpan;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

class LatencyInterceptorTest {
    @Test
    void testUrlParsing() {
        final LatencyInterceptor interceptor = new LatencyInterceptor();
        final MutableSpan span = Mockito.mock(MutableSpan.class);
        Mockito.when(span.getTag("http.query.string")).thenReturn("aa=bb&userid=test&");
        Mockito.when(span.getLocalRootSpan()).thenReturn(span);
        interceptor.onTraceComplete(Collections.singletonList(span));
        Mockito.verify(span, Mockito.atLeastOnce()).setTag(DDTags.USER_NAME, "test");
    }

    @Test
    void testUrlParsingNoUserId() {
        final LatencyInterceptor interceptor = new LatencyInterceptor();
        final MutableSpan span = Mockito.mock(MutableSpan.class);
        Mockito.when(span.getTag("http.query.string")).thenReturn("null");
        Mockito.when(span.getLocalRootSpan()).thenReturn(span);
        interceptor.onTraceComplete(Collections.singletonList(span));
        Mockito.verify(span, Mockito.never()).setTag(Mockito.eq(DDTags.USER_NAME), Mockito.anyString());
    }
}