package com.pfm.helpers;

import static com.pfm.category.CategoryController.CategoryRequest;

public class TestCategoryProvider {

  public static final CategoryRequest PARENT_CATEGORY_FOOD =
      CategoryRequest.builder()
          .name("Food")
          .build();

  public static final CategoryRequest CHILD_CATEGORY_SNICKERS =
      CategoryRequest.builder()
          .name("Snickers")
          .build();

  public static final CategoryRequest PARENT_CATEGORY_TO_ADD =
      CategoryRequest.builder()
          .name("Car")
          .build();

  public static final CategoryRequest SUBCATEGORY_TO_ADD =
      CategoryRequest.builder()
          .name("Oil")
          .build();
}