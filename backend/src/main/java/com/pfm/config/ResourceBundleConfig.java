package com.pfm.config;

import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ResourceBundleConfig {

  //category
  public static final String CANNOT_DELETE_PARENT_CATEGORY = "cannotDeleteParentCategory";
  //account validator
  public static final String EMPTY_ACCOUNT_NAME = "emptyAccountName";
  public static final String EMPTY_ACCOUNT_BALANCE = "emptyAccountBalance";
  //category validator
  public static final String EMPTY_CATEGORY_NAME = "emptyCategoryName";
  public static final String PROVIDED_PARENT_CATEGORY_NOT_EXIST = "providedParentCategoryNotExist";
  public static final String CATEGORIES_CYCLE_DETECTED = "categoryCycleDetected";
  public static final String CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST = "categoryWithProvidedNameAlreadyExist";

  @Value("${language}")
  static String language;

  private static Locale locale = new Locale(language);
  private static ResourceBundle langBundle = ResourceBundle.getBundle("messages", locale);

  public static String getMessage(String errorMessage) {
    return langBundle.getString(errorMessage);
  }
}
