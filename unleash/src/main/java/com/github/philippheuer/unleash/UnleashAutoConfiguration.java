package com.github.philippheuer.unleash;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Unleash Auto Configuration
 */
@Configuration
@ComponentScan("com.github.philippheuer.unleash")
@ConditionalOnProperty(name = "unleash.enabled", havingValue = "true", matchIfMissing = false)
public class UnleashAutoConfiguration {



}
