package com.pfm.category;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CategoryValidator {

  private static final String EMPTY_CATEGORY_NAME = "Category name is empty";
  private static final String PROVIDED_PARRENT_CATEGORY_NOT_EXIST
      = "Provided parent category not exist";

  private CategoryService categoryService;

  public List<String> validate(Category category) {
    List<String> validationResult = new ArrayList<>();

    if (category.getName() == null || category.getName() == "") {
      validationResult.add(EMPTY_CATEGORY_NAME);
    }

    if (category.getParentCategory() != null &&
        !categoryService.idExist(category.getParentCategory().getId())) {
      validationResult.add(PROVIDED_PARRENT_CATEGORY_NOT_EXIST);
    }

    return validationResult;
  }
}


