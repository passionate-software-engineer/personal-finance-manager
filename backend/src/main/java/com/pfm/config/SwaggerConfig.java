package com.pfm.config;

import com.google.common.base.Predicate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Collections;
import static com.google.common.base.Predicates.and;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger.swagger")
public class SwaggerConfig {

    private String path;

    private static final String NO_ERROR_REGEX = "(?!.*error).*$";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(paths())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "personal-finance-manager",
                "Personal Finance Manager",
                "Iteration 3",
                "Terms of service",
                new Contact("Piotr & Friends", "https://programming-in-practice.github.io", "myeaddress@company.com"),
                "License of API", "API license URL", Collections.emptyList());
    }

    private Predicate<String> paths() {
        return and(
                regex(NO_ERROR_REGEX));
    }

}
