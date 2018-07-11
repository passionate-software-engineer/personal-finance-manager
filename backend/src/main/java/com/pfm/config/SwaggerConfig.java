package com.pfm.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Setter
@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger.swagger")
public class SwaggerConfig {

    private String title;
    private String description;
    private String path;

}
