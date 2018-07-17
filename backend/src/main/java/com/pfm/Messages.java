package com.pfm;

public class Messages {

  //TODO write this properly in property file

  //account

  //category
  public static final String CANNOT_DELETE_PARENT_CATEGORY =
      "category is parent category. Delete not possible - first delete all subcategories";

  //account validator
  public static final String EMPTY_ACCOUNT_NAME = "Account name is empty";
  public static final String EMPTY_ACCOUNT_BALANCE = "Account balance is empty";

  //category validator
  public static final String EMPTY_CATEGORY_NAME = "Category name is empty";
  public static final String PROVIDED_PARRENT_CATEGORY_NOT_EXIST
      = "Provided parent category does not exist";
  public static final String CATEGORIES_CYCLE_DETECTED =
      "Detected cycle in categories structure. Category cannot be parent category for itself.";
  public static final String CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST =
      "Category with provided name already exist";

}