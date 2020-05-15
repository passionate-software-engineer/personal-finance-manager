package com.pfm.config.csv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvIngParserConfig {

  @Value("${columnSeparator}")
  public char columnSeparator;

  @Bean
  public CsvIngParserConfig parserConfig() {
    return new CsvIngParserConfig();
  }
}
