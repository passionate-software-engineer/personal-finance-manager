package com.pfm.category;

import static com.pfm.config.MessagesProvider.CATEGORIES_CYCLE_DETECTED;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_FILTER;
import static com.pfm.config.MessagesProvider.CATEGORY_IS_USED_IN_TRANSACTION;
import static com.pfm.config.MessagesProvider.CATEGORY_PRIORITY_WRONG_VALUE;
import static com.pfm.config.MessagesProvider.CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS;
import static com.pfm.config.MessagesProvider.EMPTY_CATEGORY_NAME;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST;
import static com.pfm.config.MessagesProvider.PROVIDED_PARENT_CATEGORY_ID_IS_EMPTY;
import static com.pfm.config.MessagesProvider.TOO_MANY_CATEGORY_LEVELS;
import static com.pfm.config.MessagesProvider.getMessage;

import com.pfm.filter.FilterService;
import com.pfm.transaction.TransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CategoryValidator {

  //TODO possible can simplify this L

  private CategoryService categoryService;
  private FilterService filterService;
  private TransactionService transactionService;

  public List<String> validateCategoryForUpdate(long id, long userId, Category category) {
    List<String> validationResults = new ArrayList<>();

    validate(userId, validationResults, category);

    Optional<Category> categoryToUpdate = categoryService.getCategoryByIdAndUserId(id, userId);

    if (!categoryToUpdate.isPresent()) {
      throw new IllegalStateException("Category with id: " + id + " does not exist in database");
    }

    if (!categoryToUpdate.get().getName().equals(category.getName())) {
      checkForDuplicatedName(validationResults, category, userId);
    }

    if (category.getParentCategory() != null
        && category.getParentCategory().getId() != null
        && id == category.getParentCategory().getId()) {
      validationResults.add(getMessage(CATEGORIES_CYCLE_DETECTED));
    }

    return validationResults;
  }

  public List<String> validateCategoryForAdd(Category category, long userId) {
    List<String> validationResults = new ArrayList<>();

    validate(userId, validationResults, category);

    checkForDuplicatedName(validationResults, category, userId);

    return validationResults;
  }

  private void validate(long userId, List<String> validationResults, Category category) {
    if (category.getName() == null || category.getName().trim().equals("")) {
      validationResults.add(getMessage(EMPTY_CATEGORY_NAME));
    }

    if (category.getPriority() < 1 || category.getPriority() > 1000) {
      validationResults.add(getMessage(CATEGORY_PRIORITY_WRONG_VALUE));
    }

    if (category.getParentCategory() != null && category.getParentCategory().getId() == null) {
      validationResults.add(getMessage(PROVIDED_PARENT_CATEGORY_ID_IS_EMPTY));
    }

    if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
      Optional<Category> parentCategory = categoryService.getCategoryByIdAndUserId(category.getParentCategory().getId(), userId);
      if (parentCategory.isEmpty()) {
        validationResults.add(getMessage(PROVIDED_PARENT_CATEGORY_DOES_NOT_EXIST));
      } else {
        if (parentCategory.get().getParentCategory() != null) {
          validationResults.add(getMessage(TOO_MANY_CATEGORY_LEVELS));
        }
      }
    }
  }

  private void checkForDuplicatedName(List<String> validationResults, Category category, long userId) {
    if (category.getName() != null && !category.getName().trim().equals("")
        && categoryService.isCategoryNameAlreadyUsed(category.getName(), userId)) {
      validationResults.add(getMessage(CATEGORY_WITH_PROVIDED_NAME_ALREADY_EXISTS));
    }
  }

  public List<String> validateCategoryForDelete(long categoryId) {
    List<String> validationErrors = new ArrayList<>();

    if (transactionService.transactionExistByCategoryId(categoryId)) {
      validationErrors.add(getMessage(CATEGORY_IS_USED_IN_TRANSACTION));
    }

    if (filterService.filterExistByCategoryId(categoryId)) {
      validationErrors.add(getMessage(CATEGORY_IS_USED_IN_FILTER));
    }

    return validationErrors;
  }
}
