package com.pfm.config;

import static springfox.documentation.builders.PathSelectors.any;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(getApiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.pfm"))
        .paths(any())
        .build();
  }

  private ApiInfo getApiInfo() {
    final Contact contact = new Contact("Piotr and Friends",
        "https://github.com/programming-in-practice/personal-finance-manager",
        "kolacz.piotrek@gmail.com");

    return new ApiInfoBuilder()
        .title("Personal Finance Manager")
        .description("Our app for personal finances ")
        .version("1.0.0")
        .license("Apache License 2.0")
        .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
        .contact(contact)
        .build();
  }
}