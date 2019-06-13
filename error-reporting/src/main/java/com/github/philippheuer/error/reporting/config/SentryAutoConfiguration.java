package com.github.philippheuer.error.reporting.config;

import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "sentry", name = "dsn", matchIfMissing = false)
public class SentryAutoConfiguration {

    @Value("${sentry.dsn}")
    private String sentryDsn;

    @Bean
    public SentryClient getSentryClient() {
        // Create a SentryClient instance that you manage manually.
        SentryClient sentryClient = SentryClientFactory.sentryClient(sentryDsn);
        return sentryClient;
    }

    /* Only for MVC
    @Bean
    public HandlerExceptionResolver sentryExceptionResolver() {
        return new io.sentry.spring.SentryExceptionResolver();
    }
    */

    /*
    @Bean
    public ServletContextInitializer sentryServletContextInitializer() {
        return new io.sentry.spring.SentryServletContextInitializer();
    }
    */

}
