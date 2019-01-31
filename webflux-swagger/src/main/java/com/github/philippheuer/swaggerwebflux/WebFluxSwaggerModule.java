package com.github.philippheuer.swaggerwebflux;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Swagger WebFlux Module
 */
@Configuration
@ComponentScan("com.github.philippheuer.swaggerwebflux")
@PropertySource("classpath:swagger.properties")
public class WebFluxSwaggerModule {}
