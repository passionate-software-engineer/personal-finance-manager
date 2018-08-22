package com.pfm.helpers;

import com.pfm.category.Category;
import com.pfm.category.CategoryController.CategoryRequest;

public class TestCategoryProvider {

  public static final Category CATEGORY_FOOD_NO_PARENT_CATEGORY =
      new Category(1L, "Food", null);

  public static final Category CATEGORY_CAR_NO_PARENT_CATEGORY =
      new Category(1L, "Car", null);

  public static final CategoryRequest CATEGORY_FOOD_NO_PARENT_CATEGORY_REQUEST =
      CategoryRequest.builder()
          .name("Food")
          .build();

  public static CategoryRequest getCategoryFoodNoParentCategoryRequest() {
    return CATEGORY_FOOD_NO_PARENT_CATEGORY_REQUEST;
  }
}

