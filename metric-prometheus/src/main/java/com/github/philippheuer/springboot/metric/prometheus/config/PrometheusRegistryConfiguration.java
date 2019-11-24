package com.github.philippheuer.springboot.metric.prometheus.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusRegistryConfiguration {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry(CollectorRegistry collectorRegistry, Clock clock) {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT, collectorRegistry, clock);
    }

    @Bean
    public CollectorRegistry collectorRegistry() {
        return new CollectorRegistry(true);
    }

}
