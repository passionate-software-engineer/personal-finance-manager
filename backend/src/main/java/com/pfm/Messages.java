package com.pfm;

public class Messages {

  //TODO write this properly in property file

  public static final String DELETE_CATEGORY_IS_PARENT_CATEGORY =
      "CATEGORY IS PARENT CATEGORY. DELETE NOT POSSIBLE - FIRST DELETE ALL SUBCATEGORIES";
  public static final String UPDATE_NO_ID_OR_ID_NOT_EXIST =
      "ID IS EMPTY OR THERE IS NO CATEGORY WITH PROVIDED ID";

  //category validator

  public static final String EMPTY_CATEGORY_NAME = "Category name is empty";
  public static final String PROVIDED_PARRENT_CATEGORY_NOT_EXIST
      = "Provided parent category does not exist";
}
