package com.pfm.config.csv;

import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncodingDetectorConfig {

  @Bean
  public UniversalDetector pfmEncodingDetector() {
    return new UniversalDetector(null);
  }
}
