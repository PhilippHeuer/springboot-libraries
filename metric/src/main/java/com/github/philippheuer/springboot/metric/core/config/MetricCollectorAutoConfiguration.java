package com.github.philippheuer.springboot.metric.core.config;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Metric Autoconfiguration
 * <p>
 * Enables scheduling for configurable collector tasks.
 */
@Configuration
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@EnableScheduling
@ConditionalOnProperty(prefix = "metric", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = {"com.github.philippheuer.springboot.metric"})
public class MetricCollectorAutoConfiguration {

}
