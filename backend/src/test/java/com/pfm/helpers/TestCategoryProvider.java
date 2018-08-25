package com.pfm.helpers;

import com.pfm.category.Category;
import com.pfm.category.CategoryController.CategoryRequest;

public class TestCategoryProvider {

  public static final Category CATEGORY_FOOD_NO_PARENT_CATEGORY =
      new Category(1L, "Food", null);

  public static final Category CATEGORY_CAR_NO_PARENT_CATEGORY =
      new Category(1L, "Car", null);

  public static final CategoryRequest CATEGORY_REQUEST_FOOD_NO_PARENT_CATEGORY_REQUEST =
      CategoryRequest.builder()
          .name("Food")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_CAR_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Car")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_HOME_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Home")
          .build();

  public static CategoryRequest getCategoryRequestFoodNoParentCategoryRequest() {
    return CATEGORY_REQUEST_FOOD_NO_PARENT_CATEGORY_REQUEST;
  }

  public static CategoryRequest getCategoryRequestCarNoParentCategory() {
    return CATEGORY_REQUEST_CAR_NO_PARENT_CATEGORY;
  }

  public static CategoryRequest getCategoryRequestHomeNoParentCategory() {
    return CATEGORY_REQUEST_HOME_NO_PARENT_CATEGORY;
  }
}

