package com.github.philippheuer.swaggerwebflux;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Collections;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {

    @Value("${service.name}")
    private String appName;

    @Value("${service.description}")
    private String appDescription;

    @Value("${service.version}")
    private String appVersion;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                appName,
                appDescription,
                appVersion,
                null,
                null,
                null,
                null,
                Collections.emptyList());
    }

}
