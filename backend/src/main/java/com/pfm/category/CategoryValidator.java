package com.pfm.category;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryValidator {

  private static final String EMPTY_CATEGORY_NAME = "Category name is empty";

  public List<String> validate(Category category) {
    List<String> validationResult = new ArrayList<>();

    if (category.getName() == null || category.getName() == "") {
      validationResult.add(EMPTY_CATEGORY_NAME);
    }

    return validationResult;
  }
}


