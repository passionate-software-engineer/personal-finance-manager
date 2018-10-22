package com.pfm.config;

import java.util.Locale;
import java.util.ResourceBundle;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagesProvider {

  public static final String CANNOT_DELETE_PARENT_CATEGORY = "category.cannotDeleteParentCategory";

  public static final String EMPTY_ACCOUNT_NAME = "accountValidator.emptyAccountName";
  public static final String EMPTY_ACCOUNT_BALANCE = "accountValidator.emptyAccountBalance";
  public static final String ACCOUNT_WITH_PROVIDED_NAME_ALREADY_EXISTS = "accountValidator.accountWithProvidedNameAlreadyExists";
  public static final String ACCOUNT_IS_USED_IN_TRANSACTION = "accountValidator.accountIsUsedInTransaction";
  public static final String ACCOUNT_IS_USED_IN_FILTER = "accountValidator.accountIsUsedInFilter";

  public static final String EMPTY_CATEGORY_NAME = "categoryValidator.emptyCategoryName";
  public static final String PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST = "categoryValidator.providedParentCategoryDoesNotExist";
  public static final String CATEGORIES_CYCLE_DETECTED = "categoryValidator.categoryCycleDetected";
  public static final String CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS = "categoryValidator.categoryWithProvidedNameAlreadyExists";
  public static final String CATEGORY_IS_USED_IN_TRANSACTION = "categoryValidator.categoryIsUsedInTransaction";
  public static final String CATEGORY_IS_USED_IN_FILTER = "categoryValidator.categoryIsUsedInFilter";

  public static final String EMPTY_TRANSACTION_NAME = "transactionValidator.emptyTransactionName";
  public static final String EMPTY_TRANSACTION_CATEGORY = "transactionValidator.emptyTransactionCategory";
  public static final String EMPTY_TRANSACTION_PRICE = "transactionValidator.emptyTransactionPrice";
  public static final String EMPTY_TRANSACTION_ACCOUNT = "transactionValidator.emptyTransactionAccountName";
  public static final String CATEGORY_ID_DOES_NOT_EXIST = "transactionValidator.categoryIdDoesNotExist";
  public static final String ACCOUNT_ID_DOES_NOT_EXIST = "transactionValidator.accountIdDoesNotExist";
  public static final String EMPTY_TRANSACTION_DATE = "transactionValidator.emptyDate";
  public static final String AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED = "transactionValidator.atLeastOneAccountAndPriceIsRequired";

  public static final String FILTER_ACCOUNT_ID_DOES_NOT_EXIST = "filterValidator.accountIdDoesNotExist";
  public static final String FILTER_CATEGORY_ID_DOES_NOT_EXIST = "filterValidator.categoryIdDoesNotExist";
  public static final String FILTER_PRICE_FROM_BIGGER_THEN_PRICE_TO = "filterValidator.priceFromBiggerThenPriceTo";
  public static final String FILTER_DATE_FROM_IS_AFTER_DATE_TO = "filterValidator.dateFromAfterDateTo";
  public static final String FILTER_EMPTY_NAME = "filterValidator.emptyName";

  public static final String USER_WITH_PROVIDED_USERNAME_ALREADY_EXIST = "userValidator.userWithProvidedUsernameAlreadyExists";
  public static final String EMPTY_USERNAME = "userValidator.emptyUsername";
  public static final String EMPTY_FIRST_NAME = "userValidator.emptyFirstname";
  public static final String EMPTY_LAST_NAME = "userValidator.emptyLastname";
  public static final String EMPTY_PASSWORD = "userValidator.emptyPassword";
  public static final String USERNAME_CONTAINS_WHITSPACE = "userValidator.usernameContainsWhitespaces";
  public static final String PASSWORD_CONTAINS_WHITSPACE = "userValidator.passwordContainsWhitespaces";
  public static final String USERNAME_OR_PASSWORD_IS_INCORRECT = "userController.usernameOrPassowrdIsIncorrect";

  // TODO - language should not be hardcoded any way - it must be taken from request - we need to add header language to our requests
  private static final ResourceBundle langBundle = ResourceBundle.getBundle("messages", new Locale("pl"));

  public static String getMessage(String errorMessage) {
    return langBundle.getString(errorMessage);
  }
}
