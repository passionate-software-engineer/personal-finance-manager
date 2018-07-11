package com.pfm;

public class Messages {

  //TODO write this properly in property file

  //account
  public static final String ADD_ACCOUNT_PROVIDED_ID_ALREAD_EXIST =
      "CATEGORY WITH PROVIDED ID ALREADY EXIST";
  public static final String UPDATE_ACCOUNT_NO_ID_OR_ID_NOT_EXIST =
      "ID IS EMPTY OR THERE IS NO ACCOUNT WITH PROVIDED ID";

  //category
  public static final String ADD_CATEGORY_PROVIDED_ID_ALREADY_EXIST =
      "CATEGORY WITH PROVIDED ID ALREADY EXIST";
  public static final String CANNOT_DELETE_PARENT_CATEGORY =
      "CATEGORY IS PARENT CATEGORY. DELETE NOT POSSIBLE - FIRST DELETE ALL SUBCATEGORIES";
  public static final String UPDATE_CATEGORY_NO_ID_OR_ID_NOT_EXIST =
      "ID IS EMPTY OR THERE IS NO CATEGORY WITH PROVIDED ID";

  //category validator

  public static final String EMPTY_CATEGORY_NAME = "Category name is empty";
  public static final String PROVIDED_PARRENT_CATEGORY_NOT_EXIST
      = "Provided parent category does not exist";
}
