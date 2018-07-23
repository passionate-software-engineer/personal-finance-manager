package com.pfm.config;

import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceBundleConfig {

  Locale locale = new Locale("pl");
  ResourceBundle langBundle = ResourceBundle.getBundle("messages", locale);

  public String getMessage(String errorMessage) {
    return langBundle.getString(errorMessage);
  }
}
