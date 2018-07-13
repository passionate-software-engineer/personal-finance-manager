package com.pfm.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper createObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(BigDecimal.class, new MoneySerializer());
    mapper.registerModule(module);
    return mapper;
  }

  private static final class MoneySerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider provider)
        throws IOException, JsonProcessingException {
      jsonGenerator.writeString(value.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    }
  }
}

