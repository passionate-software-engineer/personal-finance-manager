package com.pfm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class MultipartConfig {

  private static final int MB = 1024 * 1024;
  private static final int MAX_UPLOAD_SIZE_IN_MB = 5 * MB;

  @Bean
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE_IN_MB);
    return multipartResolver;
  }
}
