package com.pfm.category;

import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryValidator {

  private CategoryService categoryService;

  public List<String> validateCategoryForUpdate(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);

    // TODO - why only for update - why cycle cannot happen when creating new category?
    if (category.getParentCategory() != null
        && !categoryService
        .canBeParentCategory(category.getId(), category.getParentCategory().getId())) {
      validationResults.add(getMessage(CATEGORIES_CYCLE_DETECTED));
    }

    return validationResults;
  }

  public List<String> validateCategoryForAdd(Category category) {
    List<String> validationResults = new ArrayList<>();
    validate(validationResults, category);
    if (category.getName() != null && !category.getName().trim().equals("")
        && categoryService.isCategoryNameAlreadyUsed(category.getName())) {
      validationResults.add(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    } // TODO - why you don't check names in case of update? :)

    return validationResults;
  }

  private void validate(List<String> validationResults, Category category) {
    if (category.getName() == null || category.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_CATEGORY_NAME));
    }

    if (category.getParentCategory() != null
        && !categoryService.idExist(category.getParentCategory().getId())) {
      validationResults.add(getMessage(PROVIDED_PARENT_CATEGORY_NOT_EXIST));
    }
  }

}