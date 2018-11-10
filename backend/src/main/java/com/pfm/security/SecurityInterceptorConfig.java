package com.pfm.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class SecurityInterceptorConfig implements WebMvcConfigurer {

  private SecurityRequestInterceptor requestInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(requestInterceptor).order(Ordered.LOWEST_PRECEDENCE);
  }

}
