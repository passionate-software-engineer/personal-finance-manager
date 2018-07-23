package com.pfm.category;

import com.pfm.config.ResourceBundleConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CategoryValidator {

  private CategoryService categoryService;
  private ResourceBundleConfig resourceBundleConfig;

  public List<String> validateCategoryForUpdate(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);

    // TODO - why only for update - why cycle cannot happen when creating new category?
    if (category.getParentCategory() != null
        && !categoryService
        .canBeParentCategory(category.getId(), category.getParentCategory().getId())) {
      validationResults.add(resourceBundleConfig.getMessage("categoryCycleDetected"));
    }

    return validationResults;
  }

  public List<String> validateCategoryForAdd(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);
    if (category.getName() != null && !category.getName().trim().equals("")
        && categoryService.isCategoryNameAlreadyUsed(category.getName())) {
      validationResults.add(resourceBundleConfig.getMessage("categoryWithProvidedNameAlreadyExist"));
    } // TODO - why you don't check names in case of update? :)

    return validationResults;
  }

  private void validate(List<String> validationResults, Category category) {
    if (category.getName() == null || category.getName().trim().equals("")) {
      validationResults.add(resourceBundleConfig.getMessage("emptyCategoryName"));
    }

    if (category.getParentCategory() != null
        && !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(resourceBundleConfig.getMessage("providedParentCategoryNotExist"));
    }
  }

}