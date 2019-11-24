package com.github.philippheuer.error.reporting;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Error Reporting Module
 */
@Configuration
@ComponentScan(basePackages = "com.github.philippheuer.error.reporting")
@ConditionalOnProperty(prefix = "errorreporting", name = "enabled", havingValue = "true", matchIfMissing = false)
public class ErrorReportingAutoConfiguration {}
