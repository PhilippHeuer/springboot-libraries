package com.github.philippheuer.tracing.config;

import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.webfilter.TracingWebFilter;
import io.opentracing.contrib.spring.web.webfilter.WebFluxSpanDecorator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Configure the WebFlux Tracing Web Filter
 */
@Configuration
@ConditionalOnProperty(name = "opentracing.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(Tracer.class)
public class TracingConfiguration {

    @Bean
    public TracingWebFilter tracingWebFilter(Tracer tracer) {
        return new TracingWebFilter(
            tracer,
            Integer.MIN_VALUE,                // Order
            Pattern.compile("/actuator/*"),   // Skip pattern
            Collections.singletonList("/**"), // URL patterns
            Arrays.asList(new WebFluxSpanDecorator.StandardTags(), new WebFluxSpanDecorator.WebFluxTags())
        );
    }

}
