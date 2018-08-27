package com.pfm.helpers;

import com.pfm.category.Category;
import com.pfm.category.CategoryRequest;

public class TestCategoryProvider {

  public static final Category CATEGORY_FOOD_NO_PARENT_CATEGORY =
      Category.builder()
          .id(1L)
          .name("Food")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_FOOD_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Food")
          .build();

  public static final Category CATEGORY_CAR_NO_PARENT_CATEGORY =
      Category.builder()
          .name("Car")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_CAR_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Car")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_OIL_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Oil")
          .build();

  public static final CategoryRequest CATEGORY_REQUEST_HOME_NO_PARENT_CATEGORY =
      CategoryRequest.builder()
          .name("Home")
          .build();

  public static CategoryRequest getCategoryRequestFoodNoParentCategory() {
    return copy(CATEGORY_REQUEST_FOOD_NO_PARENT_CATEGORY);
  }

  public static CategoryRequest getCategoryRequestCarNoParentCategory() {
    return copy(CATEGORY_REQUEST_CAR_NO_PARENT_CATEGORY);
  }

  public static CategoryRequest getCategoryRequestHomeNoParentCategory() {
    return copy(CATEGORY_REQUEST_HOME_NO_PARENT_CATEGORY);
  }

  public static CategoryRequest getCategoryRequestOilNoParentCategory() {
    return copy(CATEGORY_REQUEST_OIL_NO_PARENT_CATEGORY);
  }

  private static CategoryRequest copy(CategoryRequest categoryRequest) {
    return CategoryRequest.builder()
        .name(categoryRequest.getName())
        .parentCategoryId(categoryRequest.getParentCategoryId())
        .build();
  }
}

