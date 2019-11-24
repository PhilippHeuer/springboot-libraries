package com.github.philippheuer.springboot.metric.prometheus.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "metric", name = "exporter.prometheus.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = {"com.github.philippheuer.springboot.metric.prometheus"})
public class PrometheusModuleAutoConfiguration {}
