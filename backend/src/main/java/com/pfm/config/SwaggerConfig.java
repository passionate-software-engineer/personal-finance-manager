package com.pfm.config;

import static com.pfm.config.SwaggerConfig.SECURITY_SCHEME_NAME;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
    name = SECURITY_SCHEME_NAME,
    scheme = "Bearer",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition
public class SwaggerConfig {

  public static final String SECURITY_SCHEME_NAME = "Authorization";

  @Bean
  public OpenAPI pfmOpenApi() {
    return new OpenAPI()
        .info(getApiInfo());
  }

  private Info getApiInfo() {
    final Contact contact = new Contact()
        .name("Piotr and Friends")
        .url("https://github.com/programming-in-practice/personal-finance-manager")
        .email("kolacz.piotrek@gmail.com");

    return new Info()
        .title("Personal Finance Manager")
        .description("Our app for personal finances")
        .version("1.0.0")
        .license(new License()
            .name("Apache 2.0")
            .url("http://springdoc.org"))
        .contact(contact);

  }
}
