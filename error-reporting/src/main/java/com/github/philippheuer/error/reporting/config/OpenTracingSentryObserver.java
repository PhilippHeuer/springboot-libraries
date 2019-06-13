package com.github.philippheuer.error.reporting.config;

import com.github.philippheuer.error.reporting.service.ErrorReportingService;
import io.opentracing.contrib.api.SpanData;
import io.opentracing.contrib.api.SpanObserver;
import io.opentracing.contrib.api.TracerObserver;
import io.sentry.SentryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * This class implements the {@link TracerObserver} API to observe when spans finish and records those actions in sentry.
 *
 */
@Configuration
@ConditionalOnBean(SentryClient.class)
@ConditionalOnClass(TracerObserver.class)
public class OpenTracingSentryObserver implements TracerObserver {

    @Autowired
    private ErrorReportingService errorReportingService;

    private final SentrySpanObserver spanObserver = new SentrySpanObserver();

    @Override
    public SpanObserver onStart(SpanData spanData) {
        return spanObserver;
    }

    private class SentrySpanObserver implements SpanObserver {

        @Override
        public void onSetOperationName(SpanData spanData, String operationName) {
        }

        @Override
        public void onSetTag(SpanData spanData, String key, Object value) {
        }

        @Override
        public void onSetBaggageItem(SpanData spanData, String key, String value) {
        }

        @Override
        public void onLog(SpanData spanData, long timestampMicroseconds, Map<String, ?> fields) {
        }

        @Override
        public void onLog(SpanData spanData, long timestampMicroseconds, String event) {
        }

        @Override
        public void onFinish(SpanData spanData, long finishMicros) {
            errorReportingService.recordAction(spanData.getOperationName());
        }
    }
}
