package com.github.philippheuer.swaggerwebflux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.Collections;

@Configuration
@EnableSwagger2WebFlux
@PropertySource("classpath:swagger.properties")
public class SwaggerConfiguration {

    @Value("${service.name:Unknown}")
    private String appName;

    @Value("${service.description:No description}")
    private String appDescription;

    @Value("${service.version:0.0}")
    private String appVersion;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/v[0-9]+/.*"))
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
