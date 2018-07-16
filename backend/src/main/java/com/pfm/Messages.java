package com.pfm;

public class Messages {

  //TODO write this properly in property file

  //account
  public static final String UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST =
      "id is empty or there is no account with provided id";
  public static final String ACCOUNT_WITH_ID = "Account with id = ";
  public static final String NOT_FOUND = " was not found!";
  public static final String ACCOUNT_NOT_VALID = "Passed account is not valid";

  //category
  public static final String ADD_CATEGORY_PROVIDED_ID_ALREADY_EXIST =
      "category with provided id already exist";
  public static final String CANNOT_DELETE_PARENT_CATEGORY =
      "category is parent category. Delete not possible - first delete all subcategories";
  public static final String UPDATE_CATEGORY_NO_ID_OR_ID_NOT_EXIST =
      "id is empty or there is no category with provided id";

  //account validator
  public static final String EMPTY_ACCOUNT_NAME = "Account name is empty";
  public static final String EMPTY_ACCOUNT_BALANCE = "Account balance is empty";

  //category validator
  public static final String EMPTY_CATEGORY_NAME = "Category name is empty";
  public static final String PROVIDED_PARRENT_CATEGORY_NOT_EXIST
      = "Provided parent category does not exist";
  public static final String PROVIDED_PARENT_CATEGORY_PROBLEM =
      "Category cant be self parent category and any of subcategories";
  public static final String PROVIDED_NAME_ALREADY_EXIST =
      "Category with provided name already exist";

}
