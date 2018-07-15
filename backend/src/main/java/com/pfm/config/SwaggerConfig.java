package com.pfm.config;

import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(not(regex("/error")))
        .build();
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "personal-finance-manager",
        "Personal Finance Manager",
        "v1",
        "Terms of service",
        new Contact("Programming in Practice", "https://programming-in-practice.github.io",
            "kolacz.piotrek@gmail.com"),
        "License of API", "API license URL", Collections.emptyList());
  }
}