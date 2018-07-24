package com.pfm.config;

import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceBundleConfig {

  public static final String CANNOT_DELETE_PARENT_CATEGORY = "category.cannotDeleteParentCategory";

  public static final String EMPTY_ACCOUNT_NAME = "accountValidator.emptyAccountName";
  public static final String EMPTY_ACCOUNT_BALANCE = "accountValidator.emptyAccountBalance";

  public static final String EMPTY_CATEGORY_NAME = "categoryValidator.emptyCategoryName";
  public static final String PROVIDED_PARENT_CATEGORY_NOT_EXIST = "categoryValidator.providedParentCategoryNotExist";
  public static final String CATEGORIES_CYCLE_DETECTED = "categoryValidator.categoryCycleDetected";
  public static final String CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST = "categoryValidator.categoryWithProvidedNameAlreadyExist";

  //FIXME language in null. Before added static too.
  @Value("${com.pfm.config.rbc}")
  private static String language;

  private static Locale locale = new Locale("pl");
  private static ResourceBundle langBundle = ResourceBundle.getBundle("messages", locale);

  public static String getMessage(String errorMessage) {
    return langBundle.getString(errorMessage);
  }
}
