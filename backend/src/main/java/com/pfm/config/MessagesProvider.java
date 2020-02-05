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
  public static final String ACCOUNT_CURRENCY_ID_DOES_NOT_EXIST = "accountValidator.accountCurrencyIdDoesNotExist";
  public static final String ACCOUNT_CURRENCY_NAME_DOES_NOT_EXIST = "accountValidator.accountCurrencyNameDoesNotExist";
  public static final String ACCOUNT_TYPE_ID_DOES_NOT_EXIST = "accountValidator.accountTypeIdDoesNotExist";
  public static final String ACCOUNT_TYPE_NAME_DOES_NOT_EXIST = "accountValidator.accountTypeNameDoesNotExist";

  public static final String EMPTY_CATEGORY_NAME = "categoryValidator.emptyCategoryName";
  public static final String PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST = "categoryValidator.providedParentCategoryDoesNotExist";
  public static final String CATEGORIES_CYCLE_DETECTED = "categoryValidator.categoryCycleDetected";
  public static final String CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS = "categoryValidator.categoryWithProvidedNameAlreadyExists";
  public static final String CATEGORY_IS_USED_IN_TRANSACTION = "categoryValidator.categoryIsUsedInTransaction";
  public static final String CATEGORY_IS_USED_IN_FILTER = "categoryValidator.categoryIsUsedInFilter";
  public static final String PROVIDED_PARENT_CATEGORY_ID_IS_EMPTY = "categoryValidator.parentCategoryIdIsEmpty";

  public static final String EMPTY_TRANSACTION_NAME = "transactionValidator.emptyTransactionName";
  public static final String EMPTY_TRANSACTION_CATEGORY = "transactionValidator.emptyTransactionCategory";
  public static final String EMPTY_TRANSACTION_PRICE = "transactionValidator.emptyTransactionPrice";
  public static final String EMPTY_TRANSACTION_ACCOUNT = "transactionValidator.emptyTransactionAccountName";
  public static final String FUTURE_TRANSACTION_DATE = "transactionValidator.futureTransactionDate";
  public static final String ACCOUNT_IS_ARCHIVED = "transactionValidator.accountIsArchived";
  public static final String CATEGORY_ID_DOES_NOT_EXIST = "validator.categoryIdDoesNotExist";
  public static final String ACCOUNT_ID_DOES_NOT_EXIST = "validator.accountIdDoesNotExist";
  public static final String ACCOUNT_PRICE_ENTRY_SIZE_CHANGED = "validator.accountPriceEntrySizeChangedInArchivedAccountTransaction";
  public static final String EMPTY_TRANSACTION_DATE = "transactionValidator.emptyDate";
  public static final String AT_LEAST_ONE_ACCOUNT_AND_PRICE_IS_REQUIRED = "transactionValidator.atLeastOneAccountAndPriceIsRequired";
  public static final String PRICE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED =
      "transactionValidator.archivedAccountTransactionPriceChanged";
  public static final String ACCOUNT_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED =
      "transactionValidator.archivedAccountTransactionAccountChanged";
  public static final String DATE_IN_TRANSACTION_ARCHIVED_ACCOUNT_CANNOT_BE_CHANGED = "transactionValidator.archivedAccountTransactionDateChanged";

  public static final String FILTER_ACCOUNT_ID_DOES_NOT_EXIST = "validator.accountIdDoesNotExist";
  public static final String FILTER_CATEGORY_ID_DOES_NOT_EXIST = "validator.categoryIdDoesNotExist";
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
  public static final String TOO_LONG_USERNAME = "userValidator.tooLongUsername";
  public static final String TOO_LONG_PASSWORD = "userValidator.tooLongPassword";
  public static final String TOO_LONG_FIRST_NAME = "userValidator.tooLongFirstName";
  public static final String TOO_LONG_LAST_NAME = "userValidator.tooLongLastName";
  public static final String INVALID_REFRESH_TOKEN = "tokenService.invalidRefreshToken";

  public static final String MAIN_CATEGORY = "historyInfoProvider.mainCategory";

  public static final String INTERNAL_ERROR = "internalError";
  public static final String BAD_REQUEST = "badRequest";
  public static final String IMPORT_NOT_POSSIBLE = "importValidator.importNotPossibleBecauseOfExistingData";

  private static final ResourceBundle englishBundle = ResourceBundle.getBundle("messages", new Locale("en"));
  private static final ResourceBundle polishBundle = ResourceBundle.getBundle("messages", new Locale("pl"));

  private static final ThreadLocal<Language> languageThreadLocal = new ThreadLocal<>();

  public static String getMessage(String messageKey) {
    return getMessage(messageKey, languageThreadLocal.get());
  }

  public static String getMessage(String messageKey, Language language) {
    if (Language.POLISH.equals(language)) {
      return polishBundle.getString(messageKey);
    }

    return englishBundle.getString(messageKey);
  }

  public static void setLanguage(Language language) {
    languageThreadLocal.set(language);
  }

  public enum Language {
    POLISH,
    ENGLISH;

    public static Language getEnumByString(String language) {
      if (language.contains("pl")) {
        return POLISH;
      }

      if (language.contains("en")) {
        return ENGLISH;
      }

      return ENGLISH;
    }
  }

}
