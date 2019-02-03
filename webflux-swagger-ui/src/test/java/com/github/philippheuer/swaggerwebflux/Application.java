package com.github.philippheuer.swaggerwebflux;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableSwagger2WebFlux
@Import(WebFluxSwaggerModule.class)
public class Application {

    public static void main(String[] args) {
        final SpringApplication application = new SpringApplication(Application.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }
}