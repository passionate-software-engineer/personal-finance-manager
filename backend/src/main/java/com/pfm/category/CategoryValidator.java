package com.pfm.category;

import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_NOT_EXIST;
import static com.pfm.config.MessagesProvider.getMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryValidator {

  //TODO possible can simplify this

  private CategoryService categoryService;

  public List<String> validateCategoryForUpdate(long id, long userId, Category category) {
    List<String> validationResults = new ArrayList<>();

    validate(validationResults, category);

    Optional<Category> categoryToUpdate = categoryService.getCategoryByIdAndUserId(id, userId);

    if (!categoryToUpdate.isPresent()) {
      throw new IllegalStateException("Category with id: " + id + " does not exist in database");
    }

    if (!categoryToUpdate.get().getName().equals(category.getName())) {
      checkForDuplicatedName(validationResults, category, userId);
    }

    // TODO check category.getParentCategory().getId() != null
    if (category.getParentCategory() != null && categoryService.getCategoryByIdAndUserId(category.getParentCategory().getId(), userId).isPresent()
        && !categoryService.canBeParentCategory(id, category.getParentCategory().getId(), userId)) {
      validationResults.add(getMessage(CATEGORIES_CYCLE_DETECTED));
    }

    return validationResults;
  }

  public List<String> validateCategoryForAdd(Category category, long userId) {
    List<String> validationResults = new ArrayList<>();

    validate(validationResults, category);

    checkForDuplicatedName(validationResults, category, userId);

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

  private void checkForDuplicatedName(List<String> validationResults, Category category, long userId) {
    if (category.getName() != null && !category.getName().trim().equals("")
        && categoryService.isCategoryNameAlreadyUsed(category.getName(), userId)) {
      validationResults.add(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }

}