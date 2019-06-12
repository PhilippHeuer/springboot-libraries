package com.github.philippheuer.webflux.tracing.config;

import io.opentracing.contrib.metrics.MetricsReporter;
import io.opentracing.contrib.metrics.micrometer.MicrometerMetricsReporter;
import io.opentracing.contrib.metrics.spring.autoconfigure.MetricsTracerObserverConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Auto Configuration for OpenTracing + Micrometer using the TracerObserver
 */
@Configuration
@AutoConfigureBefore(MetricsTracerObserverConfiguration.class)
@ConditionalOnClass(MicrometerMetricsReporter.class)
public class SetupTracingMetrics {

    @Bean
    public Set<MetricsReporter> setupMetricsReporter() {
        Set<MetricsReporter> reporters = new HashSet<>();

        // micrometer reporter
        reporters.add(MicrometerMetricsReporter.newMetricsReporter()
            .withName("tracing")
            .build()
        );

        return reporters;
    }

}
