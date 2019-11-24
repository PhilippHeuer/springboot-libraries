package com.github.philippheuer.tracing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger WebFlux Tracing Module
 */
@Configuration
@ComponentScan("com.github.philippheuer.tracing")
@ConditionalOnProperty(name = "opentracing.enabled", havingValue = "true", matchIfMissing = false)
public class TracingAutoConfiguration {}
