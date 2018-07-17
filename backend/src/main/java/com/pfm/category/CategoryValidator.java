package com.pfm.category;

import com.pfm.Messages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CategoryValidator {

  private CategoryService categoryService;

  public List<String> validateCategoryForUpdate(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);

    if (category.getParentCategory() != null &&
        !categoryService
            .canBeParentCategory(category.getId(), category.getParentCategory().getId())) {
      validationResults.add(Messages.CATEGORIES_CYCLE_DETECTED);
    }

    return validationResults;
  }

  public List<String> validateCategoryForAdd(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);
    if (category.getName() != null && !category.getName().equals("") && categoryService
        .nameExist(category.getName())) {
      validationResults.add(Messages.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXIST);
    }

    return validationResults;
  }

  private List<String> validate(List<String> validationResults, Category category) {
    if (category.getName() == null || category.getName().equals("")) {
      validationResults.add(Messages.EMPTY_CATEGORY_NAME);
    }

    if (category.getParentCategory() != null &&
        !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(Messages.PROVIDED_PARRENT_CATEGORY_NOT_EXIST);
    }
    return validationResults;
  }

}