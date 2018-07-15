package com.pfm.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.any;

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
                .build()
                .useDefaultResponseMessages(false)
                .globalResponseMessage((RequestMethod.GET),
                        Lists.newArrayList(
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("Successfully retrieved.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(400)
                                        .message("Bad request.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(401)
                                        .message("You are not authorized to view the resource.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("Accessing the resource you were trying to reach is forbidden")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("The resource you were trying to reach is not found")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(500)
                                        .message("Failed to process request")
                                        .build()))
                .globalResponseMessage((RequestMethod.POST),
                        Lists.newArrayList(
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("Successfully retrieved.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(201)
                                        .message("Successful creation.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(204)
                                        .message("There is no content to update.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("Accessing the resource you were trying to reach is forbidden")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("The resource you were trying to reach is not found")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(500)
                                        .message("Failed to process request")
                                        .build()))
                .globalResponseMessage((RequestMethod.DELETE),
                        Lists.newArrayList(
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("Successfully retrieved.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(204)
                                        .message("There is no content to delete.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(400)
                                        .message("Bad request.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(401)
                                        .message("You are not authorized to view the resource.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("Accessing the resource you were trying to reach is forbidden")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(404)
                                        .message("The resource you were trying to reach is not found")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(500)
                                        .message("Failed to process request")
                                        .build()))
                .globalResponseMessage((RequestMethod.PUT),
                        Lists.newArrayList(
                                new ResponseMessageBuilder()
                                        .code(200)
                                        .message("Successfully retrieved.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(201)
                                        .message("Successful creation.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(204)
                                        .message("There is no content.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(400)
                                        .message("Bad request.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(401)
                                        .message("You are not authorized to view the resource.")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(403)
                                        .message("Accessing the resource you were trying to reach is forbidden")
                                        .build(),
                                new ResponseMessageBuilder()
                                        .code(500)
                                        .message("Failed to process request")
                                        .build()));
                            }

    private ApiInfo getApiInfo() {
        final Contact contact = new Contact("Piotr and Friends",
                "https://github.com/programming-in-practice/personal-finance-manager",
                "kolacz.piotrek@gmail.com");
        return new ApiInfoBuilder()
                .title("Personal Finance Manager")
                .description("Here is the best app for personal finances ")
                .version("1.0.0")
                .license("Apache License 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
                .contact(contact)
                .build();
    }
}