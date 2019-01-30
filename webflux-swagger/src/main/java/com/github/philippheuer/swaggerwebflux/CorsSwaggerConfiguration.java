package com.github.philippheuer.swaggerwebflux;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFlux
@Configuration
public class CorsSwaggerConfiguration implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        // Swagger UI Artifacts
        corsRegistry.addMapping("/swagger-ui.html")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600);
        corsRegistry.addMapping("/v2/api-docs")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600);
        corsRegistry.addMapping("/api-docs")
                .allowedOrigins("*")
                .allowedMethods("*")
                .maxAge(3600);
    }
}