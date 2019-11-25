package com.github.philippheuer.springboot.metric.prometheus.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ConditionalOnProperty(prefix = "metric", name = "exporter.prometheus.enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = {"com.github.philippheuer.springboot.metric.prometheus"})
public class PrometheusModuleAutoConfiguration {

    /**
     * Sets ignore-resource-not-found=true to support default values in annotaitons
     *
     * @return PropertySourcesPlaceholderConfigurer
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer p =  new PropertySourcesPlaceholderConfigurer();
        p.setIgnoreResourceNotFound(true);
        return p;
    }

}
